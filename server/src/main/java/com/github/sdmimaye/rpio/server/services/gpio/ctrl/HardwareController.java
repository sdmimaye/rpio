package com.github.sdmimaye.rpio.server.services.gpio.ctrl;

import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;

public interface HardwareController {
    GpioOutput getOutputPin(int address);
    GpioInput getInputPin(int address);
}
