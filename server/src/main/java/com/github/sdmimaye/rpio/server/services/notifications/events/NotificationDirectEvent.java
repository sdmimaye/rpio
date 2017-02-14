package com.github.sdmimaye.rpio.server.services.notifications.events;

public class NotificationDirectEvent {
    private final String identifier;
    private final long user;
    private final String channel;
    private final Object argument;

    public NotificationDirectEvent(String identifier, long user, String channel, Object argument) {
        this.identifier = identifier;
        this.user = user;
        this.channel = channel;
        this.argument = argument;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Object getArgument() {
        return argument;
    }

    public long getUser() {
        return user;
    }

    public String getChannel() {
        return channel;
    }
}
