package com.github.sdmimaye.rpio.server.services.gpio.ctrl;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioTimeoutOutput;

public interface HardwareController {
    GpioOutput getOutputPin(String description, int address, GpioPinStateListener listener);
    GpioTimeoutOutput getTimeoutOutputPin(String descirption, int address, GpioPinStateListener listener, int timeout);
    GpioInput getInputPin(String description, int address, GpioPinStateListener listener);
}
