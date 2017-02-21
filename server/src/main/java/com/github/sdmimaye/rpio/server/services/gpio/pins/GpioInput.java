package com.github.sdmimaye.rpio.server.services.gpio.pins;

import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;

public interface GpioInput extends Gpio {
    @Override
    default PinMode getPinMode() {
        return PinMode.INPUT;
    }
}
