package com.github.sdmimaye.rpio.server.services;

import com.github.sdmimaye.rpio.common.services.RpioServiceList;
import com.github.sdmimaye.rpio.common.utils.state.ApplicationState;
import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ApplicationState.class).to(ServerApplicationState.class);
        bind(RpioServiceList.class).to(ServerServiceList.class);
    }
}
