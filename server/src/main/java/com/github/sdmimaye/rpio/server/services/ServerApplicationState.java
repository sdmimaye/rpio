package com.github.sdmimaye.rpio.server.services;

import com.github.sdmimaye.rpio.common.utils.state.ApplicationState;

public class ServerApplicationState implements ApplicationState {
    @Override
    public boolean isApplicationConfigurationDone() {
        return true;
    }
}
