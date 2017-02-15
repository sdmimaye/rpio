package com.github.sdmimaye.rpio.server.services.gpio.pins;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;

public interface GpioOutput extends Gpio {
    void setState(GpioPinState state);
}
