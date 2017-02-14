package com.github.sdmimaye.rpio.server.services.gcm.messages;

import com.github.sdmimaye.rpio.server.services.notifications.messaging.NotificationMessage;

public class GcmNotificationMessage extends GcmTargetedMessage{
    private final String receiverId;
    private final NotificationMessage message;

    public GcmNotificationMessage(String receiverId, NotificationMessage message) {
        this.receiverId = receiverId;
        this.message = message;
    }

    @Override
    protected String to() {
        return receiverId;
    }

    @Override
    public Object data() {
        return message;
    }
}
