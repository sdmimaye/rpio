package com.github.sdmimaye.rpio.server.services.notifications.channels;

import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.services.gcm.GcmService;
import com.github.sdmimaye.rpio.server.services.gcm.messages.GcmNotificationMessage;
import com.github.sdmimaye.rpio.server.services.notifications.messaging.NotificationMessage;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class GcmChannel extends Channel{
    private final GcmService service;

    @Inject
    public GcmChannel(GcmService service) {
        this.service = service;
    }

    @Override
    public void send(User receiver, NotificationMessage message) {
        service.send(new GcmNotificationMessage(receiver.getSenderId(), message));
    }

    @Override
    public boolean isAvailable(User receiver) {
        return service.isAvailable() && !StringUtils.isEmpty(receiver.getSenderId());
    }
}
