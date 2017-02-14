package com.github.sdmimaye.rpio.server.services;

import com.github.sdmimaye.rpio.common.utils.state.ApplicationState;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.google.inject.Inject;

public class ServerApplicationState implements ApplicationState {
    private final HibernateUtil util;

    @Inject
    public ServerApplicationState(HibernateUtil util) {
        this.util = util;
    }

    @Override
    public boolean isApplicationConfigurationDone() {
        return util.isDatabaseInitialized();
    }
}
