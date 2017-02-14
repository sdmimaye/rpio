package com.github.sdmimaye.rpio.server.services.notifications.events;

import com.github.sdmimaye.rpio.server.database.models.system.User;

public interface NotificationBeforeSendEvaluator {
    boolean doEvaluate(Object parameters, User user);
}
