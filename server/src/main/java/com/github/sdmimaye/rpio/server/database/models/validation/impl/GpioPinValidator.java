package com.github.sdmimaye.rpio.server.database.models.validation.impl;

import com.github.sdmimaye.rpio.server.database.dao.gpio.GpioPinDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.gpio.GpioPin;
import com.github.sdmimaye.rpio.server.database.models.validation.ModelValidator;
import com.github.sdmimaye.rpio.server.database.models.validation.ValidationError;
import com.github.sdmimaye.rpio.server.database.models.validation.ValidationErrorListBuilder;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadableGpioPin;
import com.google.inject.Inject;

public class GpioPinValidator extends ModelValidator<GpioPin, ReadableGpioPin, GpioPinDao> {
    @Inject
    public GpioPinValidator(GpioPinDao pinDao, HibernateUtil util) {
        super(pinDao, util);
    }

    @Override
    protected GpioPin doInsert(ReadableGpioPin model, ValidationErrorListBuilder builder) {
        if(model.getNumber() == null)
            builder.with("missingNumber", model);

        if(model.getMode() == null)
            builder.with("missingMode", model);

        GpioPin byNumber = dao.getByNumber(model.getNumber());
        if(byNumber != null)
            builder.with("pinInUse", model);

        GpioPin byDescription = dao.getByDescription(model.getDescription());
        if(byDescription != null)
            builder.with("descriptionInUse", model);

        GpioPin pin = dao.create();
        pin.setMode(model.getMode());
        pin.setNumber(model.getNumber());

        dao.save(pin);
        return pin;
    }

    @Override
    protected GpioPin doUpdate(long id, ReadableGpioPin model, ValidationErrorListBuilder builder) {
        if(model.getNumber() == null)
            builder.with("missingNumber", model);

        if(model.getMode() == null)
            builder.with("missingMode", model);

        GpioPin byId = dao.getById(model.getId());
        if(byId == null)
            ValidationError.critical("notFound");

        GpioPin byNumber = dao.getByNumber(model.getNumber());
        if(byNumber != byId)
            builder.with("pinInUse", model);

        GpioPin byDescription = dao.getByDescription(model.getDescription());
        if(byDescription != byId)
            builder.with("descriptionInUse", model);

        byId.setNumber(model.getNumber());
        byId.setMode(model.getMode());

        return byId;
    }

    @Override
    protected GpioPin doDelete(long id, ValidationErrorListBuilder builder) {
        GpioPin byId = dao.getById(id);
        if(byId == null)
            ValidationError.critical("notFound");

        dao.delete(byId);
        return byId;
    }
}
