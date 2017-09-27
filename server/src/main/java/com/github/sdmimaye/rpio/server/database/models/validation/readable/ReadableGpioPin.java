package com.github.sdmimaye.rpio.server.database.models.validation.readable;

import com.github.sdmimaye.rpio.server.database.models.enums.PinLogic;
import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import com.github.sdmimaye.rpio.server.database.models.enums.PinOuputMode;

public interface ReadableGpioPin extends ReadableEntity {
    Integer getNumber();
    PinMode getMode();
    String getDescription();
    PinOuputMode getOuputMode();
    PinLogic getLogic();
    Integer getTimeout();
}
