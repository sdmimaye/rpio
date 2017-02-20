package com.github.sdmimaye.rpio.server.services.gpio.pins;

import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;

import java.io.Closeable;

public interface Gpio extends Closeable {
    int getNumber();
    PinMode getPinMode();
    String getDescription();
    GpioPinState getState();
}
