package com.github.sdmimaye.rpio.server.services.gpio.pins;

import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;

public interface GpioOutput extends Gpio {
    void setState(GpioPinState state);

    @Override
    default PinMode getPinMode() {
        return PinMode.OUTPUT;
    }
    default void change(){
        switch (getState()) {
            case HIGH:
                setState(GpioPinState.LOW);
                break;
            case LOW:
                setState(GpioPinState.HIGH);
                break;
        }
    }
}
