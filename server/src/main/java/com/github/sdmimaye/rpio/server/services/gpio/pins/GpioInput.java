package com.github.sdmimaye.rpio.server.services.gpio.pins;

import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;

public interface GpioInput extends Gpio {
    void register(GpioPinStateListener listener);

    @Override
    default PinMode getPinMode() {
        return PinMode.INPUT;
    }
}
