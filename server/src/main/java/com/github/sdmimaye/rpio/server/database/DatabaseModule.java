package com.github.sdmimaye.rpio.server.database;

import com.github.sdmimaye.rpio.server.database.hibernate.HibernateSessionProvider;
import com.google.inject.AbstractModule;
import org.hibernate.Session;

public class DatabaseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Session.class).toProvider(HibernateSessionProvider.class);
        requestStaticInjection(HibernateRevisionListener.class);
    }
}
