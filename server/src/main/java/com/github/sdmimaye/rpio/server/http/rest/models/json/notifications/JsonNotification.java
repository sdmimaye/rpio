package com.github.sdmimaye.rpio.server.http.rest.models.json.notifications;


import com.github.sdmimaye.rpio.server.database.models.notifications.Notification;
import com.github.sdmimaye.rpio.server.database.models.notifications.NotificationSubscription;
import com.github.sdmimaye.rpio.server.database.models.system.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonNotification {
    public static final String UNSUED_CHANNEL_IDENTIFIER = "NONE";

    private Long id;
    private String name;
    private String channel;
    private String subject;
    private String message;

    public static List<JsonNotification> convert(User user, List<Notification> notifications) {
        List<JsonNotification> result = new ArrayList<>();
        notifications.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        for (Notification notification : notifications) {
            JsonNotification n = new JsonNotification();
            n.setName(notification.getName());
            n.setId(notification.getId());
            n.setSubject(notification.getSubject());
            n.setMessage(notification.getMessage());

            Optional<NotificationSubscription> first = user.getSubscriptions().stream().filter(s -> s.getNotification() == notification).findFirst();
            if(first == null || !first.isPresent()) {//user does not subscribe to this
                n.setChannel(UNSUED_CHANNEL_IDENTIFIER);
            }else {//user does subscribe to this
                NotificationSubscription subscription = first.get();
                n.setChannel(subscription.getChannel().getDescription());
            }
            result.add(n);
        }

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
