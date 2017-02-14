package com.github.sdmimaye.rpio.server.database.hibernate;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.hibernate.Session;

public class HibernateSessionProvider implements Provider<Session> {
    private final HibernateUtil hibernateUtil;

    @Inject
    public HibernateSessionProvider(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    @Override
    public Session get() {
        return hibernateUtil.getSession();
    }
}
