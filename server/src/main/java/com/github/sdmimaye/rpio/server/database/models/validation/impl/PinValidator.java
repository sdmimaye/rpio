package com.github.sdmimaye.rpio.server.database.models.validation.impl;

import com.github.sdmimaye.rpio.server.database.dao.gpio.PinDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.gpio.Pin;
import com.github.sdmimaye.rpio.server.database.models.validation.ModelValidator;
import com.github.sdmimaye.rpio.server.database.models.validation.ValidationError;
import com.github.sdmimaye.rpio.server.database.models.validation.ValidationErrorListBuilder;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadablePin;
import com.google.inject.Inject;

public class PinValidator extends ModelValidator<Pin, ReadablePin, PinDao> {
    @Inject
    public PinValidator(PinDao pinDao, HibernateUtil util) {
        super(pinDao, util);
    }

    @Override
    protected Pin doInsert(ReadablePin model, ValidationErrorListBuilder builder) {
        if(model.getNumber() == null)
            builder.with("missingNumber", model);

        if(model.getMode() == null)
            builder.with("missingMode", model);

        Pin byNumber = dao.getByNumber(model.getNumber());
        if(byNumber != null)
            builder.with("pinInUse", model);

        Pin pin = dao.create();
        pin.setMode(model.getMode());
        pin.setNumber(model.getNumber());

        dao.save(pin);
        return pin;
    }

    @Override
    protected Pin doUpdate(long id, ReadablePin model, ValidationErrorListBuilder builder) {
        if(model.getNumber() == null)
            builder.with("missingNumber", model);

        if(model.getMode() == null)
            builder.with("missingMode", model);

        Pin byId = dao.getById(model.getId());
        if(byId == null)
            ValidationError.critical("notFound");

        Pin byNumber = dao.getByNumber(model.getNumber());
        if(byNumber != byId)
            builder.with("pinInUse", model);

        byId.setNumber(model.getNumber());
        byId.setMode(model.getMode());

        return byId;
    }

    @Override
    protected Pin doDelete(long id, ValidationErrorListBuilder builder) {
        Pin byId = dao.getById(id);
        if(byId == null)
            ValidationError.critical("notFound");

        dao.delete(byId);
        return byId;
    }
}
