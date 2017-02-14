package com.github.sdmimaye.rpio.server.services.notifications.channels;

import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.services.notifications.messaging.NotificationMessage;
import com.google.inject.Singleton;

@Singleton
public class OnScreenChannel extends Channel{
    private Interceptor interceptor;

    public static String getChannelDescription() {
        Class type = OnScreenChannel.class;
        return type.getSimpleName();
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void send(User receiver, NotificationMessage message) {
        if(!isAvailable(receiver))
            return;

        interceptor.send(receiver, message);
    }

    @Override
    public boolean isAvailable(User receiver) {
        if(interceptor == null)
            return false;

        return interceptor.isConnected(receiver);
    }

    public interface Interceptor{
        boolean isConnected(User user);
        void send(User user, NotificationMessage message);
    }
}
