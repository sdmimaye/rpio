package com.github.sdmimaye.rpio.server.services.gpio.ctrl.rpi;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioTimeoutOutput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

public class RpiGpioTimeoutOuput extends RpiGpioOuput implements GpioTimeoutOutput {
    private final int timeout;

    public RpiGpioTimeoutOuput(GpioPinDigitalOutput pin, GpioPinStateListener listener, int timeout) {
        super(pin, listener);
        this.timeout = timeout;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }
}

