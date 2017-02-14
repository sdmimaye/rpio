package com.github.sdmimaye.rpio.server.database.models.notifications;

import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Audited
public class NotificationSubscription extends PersistedEntityBase {
    @ManyToOne
    private User subscriber;
    @ManyToOne
    private Notification notification;
    @ManyToOne
    private NotificationChannel channel;

    public User getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(User subscriber) {
        this.subscriber = subscriber;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "NotificationSubscription{" +
                "subscriber=" + subscriber +
                ", notification=" + notification +
                ", channel=" + channel +
                '}';
    }
}
