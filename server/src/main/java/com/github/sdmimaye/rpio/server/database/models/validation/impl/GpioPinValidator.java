package com.github.sdmimaye.rpio.server.database.models.validation.impl;

import com.github.sdmimaye.rpio.server.database.dao.gpio.GpioPinDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import com.github.sdmimaye.rpio.server.database.models.enums.PinOuputMode;
import com.github.sdmimaye.rpio.server.database.models.gpio.GpioPin;
import com.github.sdmimaye.rpio.server.database.models.validation.ModelValidator;
import com.github.sdmimaye.rpio.server.database.models.validation.ValidationError;
import com.github.sdmimaye.rpio.server.database.models.validation.ValidationErrorListBuilder;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadableGpioPin;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class GpioPinValidator extends ModelValidator<GpioPin, ReadableGpioPin, GpioPinDao> {
    @Inject
    public GpioPinValidator(GpioPinDao pinDao, HibernateUtil util) {
        super(pinDao, util);
    }

    @Override
    protected GpioPin doInsert(ReadableGpioPin model, ValidationErrorListBuilder builder) {
        if(StringUtils.isEmpty(model.getDescription()))
            builder.with("missingDescription");

        if(model.getNumber() == null)
            builder.with("missingNumber");

        if(model.getMode() == null)
            builder.with("missingMode");

        GpioPin byNumber = dao.getByNumber(model.getNumber());
        if(byNumber != null)
            builder.with("pinInUse");

        GpioPin byDescription = dao.getByDescription(model.getDescription());
        if(byDescription != null)
            builder.with("descriptionInUse");

        if(model.getMode() == PinMode.OUTPUT && model.getOuputMode() == null)
            builder.with("missingOutputMode");

        if(model.getMode() == PinMode.OUTPUT && model.getOuputMode() == PinOuputMode.TIMEOUT && model.getTimeout() == null)
            builder.with("missingTimeout");

        GpioPin pin = dao.create();
        pin.setMode(model.getMode());
        pin.setNumber(model.getNumber());
        pin.setDescription(model.getDescription());
        pin.setOuputMode(model.getOuputMode());
        pin.setTimeout(model.getTimeout());

        dao.save(pin);
        return pin;
    }

    @Override
    protected GpioPin doUpdate(long id, ReadableGpioPin model, ValidationErrorListBuilder builder) {
        if(StringUtils.isEmpty(model.getDescription()))
            builder.with("missingDescription");

        if(model.getNumber() == null)
            builder.with("missingNumber");

        if(model.getMode() == null)
            builder.with("missingMode");

        GpioPin byId = dao.getById(model.getId());
        if(byId == null)
            ValidationError.critical("notFound");

        GpioPin byNumber = dao.getByNumber(model.getNumber());
        if(byNumber != null && byNumber != byId)
            builder.with("pinInUse");

        GpioPin byDescription = dao.getByDescription(model.getDescription());
        if(byDescription != null && byDescription != byId)
            builder.with("descriptionInUse");

        if(model.getMode() == PinMode.OUTPUT && model.getOuputMode() == null)
            builder.with("missingOutputMode");

        if(model.getMode() == PinMode.OUTPUT && model.getOuputMode() == PinOuputMode.TIMEOUT && model.getTimeout() == null)
            builder.with("missingTimeout");

        byId.setNumber(model.getNumber());
        byId.setMode(model.getMode());
        byId.setDescription(model.getDescription());
        byId.setOuputMode(model.getOuputMode());
        byId.setTimeout(model.getTimeout());

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
