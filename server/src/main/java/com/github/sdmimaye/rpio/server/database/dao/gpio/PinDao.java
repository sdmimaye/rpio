package com.github.sdmimaye.rpio.server.database.dao.gpio;

import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.models.gpio.Pin;
import com.google.inject.Provider;
import org.hibernate.Session;

import javax.inject.Inject;

public class PinDao extends HibernateDaoBase<Pin> {
    @Inject
    public PinDao(Provider<Session> session) {
        super(session);
    }

    @Override
    protected Class<Pin> getModelClass() {
        return Pin.class;
    }

    public Pin getByNumber(Integer number) {
        return (Pin) createQuery("from Pin where number = :number").setInteger("number", number).setMaxResults(1).uniqueResult();
    }
}
