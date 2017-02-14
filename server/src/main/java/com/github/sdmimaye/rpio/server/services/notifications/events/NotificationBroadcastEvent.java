package com.github.sdmimaye.rpio.server.services.notifications.events;

import com.github.sdmimaye.rpio.server.database.models.system.User;

public class NotificationBroadcastEvent {
    private final String identifier;
    private final Object argument;
    private final NotificationBeforeSendEvaluator evaluator;

    public NotificationBroadcastEvent(String identifier, Object argument) {
        this.identifier = identifier;
        this.argument = argument;
        this.evaluator = null;
    }

    public NotificationBroadcastEvent(String identifier, Object argument, NotificationBeforeSendEvaluator evaluator) {
        this.identifier = identifier;
        this.argument = argument;
        this.evaluator = evaluator;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Object getArgument() {
        return argument;
    }

    public boolean evaluate(User user) {
        return evaluator == null || evaluator.doEvaluate(argument, user);
    }
}
