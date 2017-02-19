package com.github.sdmimaye.rpio.server.database.dao.gpio;

import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.models.gpio.GpioPin;
import com.google.inject.Provider;
import org.hibernate.Session;

import javax.inject.Inject;

public class GpioPinDao extends HibernateDaoBase<GpioPin> {
    @Inject
    public GpioPinDao(Provider<Session> session) {
        super(session);
    }

    @Override
    protected Class<GpioPin> getModelClass() {
        return GpioPin.class;
    }

    public GpioPin getByNumber(Integer number) {
        return (GpioPin) createQuery("from GpioPin where number = :number").setInteger("number", number).setMaxResults(1).uniqueResult();
    }

    public GpioPin getByDescription(String description) {
        return (GpioPin) createQuery("from GpioPin where description = :description").setString("description", description).setMaxResults(1).uniqueResult();
    }
}
