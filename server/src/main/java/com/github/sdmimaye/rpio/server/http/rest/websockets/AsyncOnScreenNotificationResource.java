package com.github.sdmimaye.rpio.server.http.rest.websockets;

import com.github.sdmimaye.rpio.common.utils.threads.ThreadUtils;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.http.rest.util.UserSessionUtil;
import com.github.sdmimaye.rpio.server.services.notifications.channels.OnScreenChannel;
import com.github.sdmimaye.rpio.server.services.notifications.messaging.NotificationMessage;
import com.google.inject.Inject;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@ManagedService(path = "/async/notifications")
@PermitAll
public class AsyncOnScreenNotificationResource implements OnScreenChannel.Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(AsyncOnScreenNotificationResource.class);
    private final OnScreenChannel onScreenChannel;
    private final HibernateUtil hibernateUtil;
    private final UserSessionUtil userSessionUtil;

    private final List<AtmosphereResource> connections = new ArrayList<>();
    private final Thread worker;
    private final Object lock = new Object();

    @Inject
    public AsyncOnScreenNotificationResource(OnScreenChannel onScreenChannel, HibernateUtil hibernateUtil, UserSessionUtil userSessionUtil) {
        this.onScreenChannel = onScreenChannel;
        this.hibernateUtil = hibernateUtil;
        this.userSessionUtil = userSessionUtil;
        this.onScreenChannel.setInterceptor(this);

        worker = new Thread(() ->{//send empty message to prevent timeout
            while (ThreadUtils.sleep(30000)){
                synchronized (lock){
                    for (AtmosphereResource connection : connections) {
                        send(connection, new NotificationMessage());
                    }
                }
            }
        });
        worker.start();
    }

    @Ready
    public final void onReady(final AtmosphereResource resource) {
        synchronized (lock) {
            connections.add(resource);
        }
        logger.info("AsyncOnScreenNotificationResource {} connected", resource.uuid());
    }

    @Disconnect
    public final void onDisconnect(final AtmosphereResourceEvent event) {
        synchronized (lock) {
            connections.remove(event.getResource());
        }

        if (event.isCancelled())
            logger.info("AsyncOnScreenNotificationResource {} unexpectedly disconnected", event.getResource().uuid());
        else if (event.isClosedByClient())
            logger.info("AsyncOnScreenNotificationResource {} closed the connection", event.getResource().uuid());
    }

    @Override
    public boolean isConnected(User user) {
        if(user == null)
            return false;

        AtmosphereResource resource = getResourceForUser(user);
        return resource != null;
    }

    @Override
    public void send(User receiver, NotificationMessage message) {
        if(receiver == null || message == null)
            return;

        AtmosphereResource resource = getResourceForUser(receiver);
        if(resource == null)
            return;

        send(resource, message);
    }

    private void send(AtmosphereResource resource, Object data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = mapper.writeValueAsString(data);
            resource.write(message);
        } catch (Exception ignored) {

        }
    }

    private AtmosphereResource getResourceForUser(User user) {
        return hibernateUtil.doWork((Callable<AtmosphereResource>) () -> {
            List<AtmosphereResource> copy;
            synchronized (lock) {
                copy = new ArrayList<>(connections);
            }
            for (AtmosphereResource connection : copy) {
                try {
                    User current = userSessionUtil.loadUser(connection.session());
                    if (current == user)
                        return connection;
                } catch (Exception ex) {
                    logger.info("Lost connection to client: {}. Message was lost", user.getLoginName());
                    connections.remove(connection);
                }
            }

            return null;
        });
    }
}