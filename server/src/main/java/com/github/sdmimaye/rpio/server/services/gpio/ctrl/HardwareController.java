package com.github.sdmimaye.rpio.server.services.gpio.ctrl;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;

public interface HardwareController {
    GpioOutput getOutputPin(String description, int address, GpioPinStateListener listener);
    GpioInput getInputPin(String description, int address, GpioPinStateListener listener);
}
