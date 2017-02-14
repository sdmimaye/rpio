package com.github.sdmimaye.rpio.server.services.notifications.channels;


import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.services.notifications.messaging.NotificationMessage;

public abstract class Channel {
    public final String getDescription() {
        Class type = this.getClass();
        return type.getSimpleName();
    }

    public abstract void send(User receiver, NotificationMessage message);
    public abstract boolean isAvailable(User receiver);
}
