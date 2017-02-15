package com.github.sdmimaye.rpio.server.services.notifications;

import com.github.sdmimaye.rpio.common.services.RpioService;
import com.github.sdmimaye.rpio.common.utils.threads.ThreadUtils;
import com.github.sdmimaye.rpio.server.database.dao.notifications.NotificationChannelDao;
import com.github.sdmimaye.rpio.server.database.dao.notifications.NotificationDao;
import com.github.sdmimaye.rpio.server.database.dao.system.UserDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.notifications.Notification;
import com.github.sdmimaye.rpio.server.database.models.notifications.NotificationChannel;
import com.github.sdmimaye.rpio.server.database.models.notifications.NotificationSubscription;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.services.notifications.channels.Channel;
import com.github.sdmimaye.rpio.server.services.notifications.channels.EmailChannel;
import com.github.sdmimaye.rpio.server.services.notifications.channels.GcmChannel;
import com.github.sdmimaye.rpio.server.services.notifications.channels.OnScreenChannel;
import com.github.sdmimaye.rpio.server.services.notifications.events.NotificationBeforeSendEvaluator;
import com.github.sdmimaye.rpio.server.services.notifications.events.NotificationBroadcastEvent;
import com.github.sdmimaye.rpio.server.services.notifications.events.NotificationDirectEvent;
import com.github.sdmimaye.rpio.server.services.notifications.messaging.NotificationMessage;
import com.github.sdmimaye.rpio.server.services.notifications.messaging.NotificationMessageParser;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;

@Singleton
public class NotificationService implements RpioService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final Queue<NotificationBroadcastEvent> broadcasts = new ConcurrentLinkedDeque<>();
    private final Queue<NotificationDirectEvent> direct = new ConcurrentLinkedDeque<>();
    private final List<Timeout> timeouts = new ArrayList<>();
    private final List<Channel> channels;
    private final Object channelMutex = new Object();

    private final UserDao userDao;
    private final NotificationMessageParser parser;
    private final NotificationDao notificationDao;
    private final NotificationChannelDao notificationChannelDao;
    private final HibernateUtil hibernateUtil;

    @Inject
    public NotificationService(Injector injector, UserDao userDao, NotificationMessageParser parser, NotificationDao notificationDao,
                               NotificationChannelDao notificationChannelDao, HibernateUtil hibernateUtil) {
        this.userDao = userDao;
        this.parser = parser;
        this.notificationDao = notificationDao;
        this.notificationChannelDao = notificationChannelDao;
        this.hibernateUtil = hibernateUtil;

        channels = Arrays.asList(
                injector.getInstance(OnScreenChannel.class),
                injector.getInstance(EmailChannel.class),
                injector.getInstance(GcmChannel.class));

        writeChannelsToDb(channels);
    }

    private void writeChannelsToDb(List<Channel> channels) {
        hibernateUtil.doWork(() -> {
            for (Channel channel : channels) {
                NotificationChannel current = notificationChannelDao.getByDescription(channel.getDescription());
                if (current != null)
                    continue;

                NotificationChannel newChannel = new NotificationChannel();
                newChannel.setDescription(channel.getDescription());
                notificationChannelDao.save(newChannel);
            }
        });
    }

    @Override
    public void cleanup() {
        broadcasts.clear();
    }

    private void doBroadcasts(){
        NotificationBroadcastEvent e = broadcasts.poll();
        if (e == null)
            return;

        hibernateUtil.doWork(() -> {
            Notification notification = getByNameOrAddWorkerless(e.getIdentifier());
            if (!notification.isEnabled())
                return;

            try {
                NotificationMessage message = parser.parse(notification, e.getArgument());
                if (message == null)
                    return;

                for (NotificationSubscription subscription : notification.getSubscriptions()) {
                    Channel channel = getChannelForSubscription(subscription);
                    if (channel == null)
                        return;

                    if(e.evaluate(subscription.getSubscriber()))
                        channel.send(subscription.getSubscriber(), message);
                }
            } catch (Exception ex) {
                logger.warn("Error while sending broadcast notification", ex);
            }
        });
    }

    private void doDirects(){
        NotificationDirectEvent e = direct.poll();
        if(e == null)
            return;

        hibernateUtil.doWork(() ->{
            User user = userDao.getById(e.getUser());
            if(user == null)
                throw new RuntimeException("Could not resolve user for notification");

            Notification notification = getByNameOrAddWorkerless(e.getIdentifier());
            try {
                NotificationMessage message = parser.parse(notification, e.getArgument());
                if (message == null)
                    return;

                synchronized (channelMutex) {
                    Optional<Channel> channelOptional = channels.stream()
                                                                .filter(c -> c.getDescription().equals(e.getChannel()))
                                                                .findFirst();
                    if (channelOptional == null || !channelOptional.isPresent())
                        throw new RuntimeException("Could not find channel: " + e.getChannel());

                    Channel channel = channelOptional.get();
                    channel.send(user, message);
                }
            } catch (Exception ex) {
                logger.warn("Error while sending direct notification", ex);
            }
        });
    }

    @Override
    public void run() {
        while (ThreadUtils.sleep(100)) {
            doBroadcasts();
            doDirects();
        }
    }

    private Channel getChannelForSubscription(NotificationSubscription subscription) {
        NotificationChannel selected = subscription.getChannel();
        if (selected == null)
            throw new RuntimeException("No Channeld defined for subscription: " + subscription.toString());

        synchronized (channelMutex) {
            Optional<Channel> channelOptional = channels.stream()
                                                        .filter(c -> c.getDescription()
                                                                      .equals(selected.getDescription()))
                                                        .findFirst();
            if (channelOptional == null || !channelOptional.isPresent())
                throw new RuntimeException("Could not find channel: " + selected.getDescription());

            return channelOptional.get();
        }
    }

    private boolean isSendRequired(String identifier, int timeoutInMilliseconds) {
        Optional<Timeout> first = timeouts.stream().filter(t -> t.getIdentifier().equals(identifier)).findFirst();
        if (first.isPresent()) {//we got an event with that identifier
            Timeout timeout = first.get();
            if (!timeout.isTimeouted())//we got one, but its still active -> suppress message
                return false;

            //we got one, its timeouted -> return true and reset the timer
            timeout.reset(timeoutInMilliseconds);
            return true;
        }else {//never send this message -> create new entry and return true
            timeouts.add(new Timeout(identifier, timeoutInMilliseconds));
            return true;
        }
    }

    public void direct(String identifier, Object parameter, int timeout, String channel, long userId){
        if(isSendRequired(identifier, timeout))
            direct.offer(new NotificationDirectEvent(identifier, userId, channel, parameter));
    }

    public void broadcast(String identifier, Object parameter, int timeoutInMilliseconds) {
        if(isSendRequired(identifier, timeoutInMilliseconds))
            broadcasts.offer(new NotificationBroadcastEvent(identifier, parameter));
    }

    public void broadcast(String identifier, Object parameter, int timeoutInMilliseconds, NotificationBeforeSendEvaluator evaluator) {
        if(isSendRequired(identifier, timeoutInMilliseconds))
            broadcasts.offer(new NotificationBroadcastEvent(identifier, parameter, evaluator));
    }

    private Notification getByNameOrAddWorkerless(String identifier) {
        return register(identifier);
    }

    public Notification register(String notification) {
        return hibernateUtil.doWork((Callable<Notification>) () -> {
            Notification current = notificationDao.getByName(notification);
            if (current != null)
                return current;

            current = new Notification();
            current.setName(notification);
            notificationDao.save(current);
            hibernateUtil.commitAndClose();

            return current;
        });
    }

    public Notification[] register(String... notifications) {
        Notification[] result = new Notification[notifications.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = register(notifications[i]);
        }

        return result;
    }

    public boolean isChannelAvailable(User user, String channelName) {
        synchronized (channelMutex) {
            return channels.stream().anyMatch(c -> c.getDescription().equals(channelName) && c.isAvailable(user));
        }
    }

    private static class Timeout {
        private final String identifier;
        private LocalDateTime last;
        private LocalDateTime end;

        private Timeout(String identifier, int timeoutInMilliseconds) {
            this.identifier = identifier;
            this.last = LocalDateTime.now();
            this.end = last.plusSeconds(timeoutInMilliseconds * 1000);
        }

        public String getIdentifier() {
            return identifier;
        }

        public boolean isTimeouted() {
            return LocalDateTime.now().isAfter(end);
        }

        public void reset(int timeoutInMilliseconds) {
            last = LocalDateTime.now();
            this.end = last.plusSeconds(timeoutInMilliseconds * 1000);
        }
    }
}
