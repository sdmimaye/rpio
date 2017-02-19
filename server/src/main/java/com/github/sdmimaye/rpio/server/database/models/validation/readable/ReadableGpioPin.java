package com.github.sdmimaye.rpio.server.database.models.validation.readable;

import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;

public interface ReadableGpioPin extends ReadableEntity {
    Integer getNumber();
    PinMode getMode();
    String getDescription();
}
