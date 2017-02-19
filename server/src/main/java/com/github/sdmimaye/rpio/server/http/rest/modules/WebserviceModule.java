package com.github.sdmimaye.rpio.server.http.rest.modules;


import com.github.sdmimaye.rpio.server.http.rest.resources.*;
import com.google.inject.Binder;
import com.google.inject.Module;

public class WebserviceModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(ActivityLogResource.class);
        binder.bind(ConfigResource.class);
        binder.bind(LogFileResource.class);
        binder.bind(NotificationResource.class);
        binder.bind(RoleResource.class);
        binder.bind(GpioPinResource.class);
        binder.bind(ServerStatusResource.class);
        binder.bind(SessionResource.class);
        binder.bind(UserResource.class);
    }
}
