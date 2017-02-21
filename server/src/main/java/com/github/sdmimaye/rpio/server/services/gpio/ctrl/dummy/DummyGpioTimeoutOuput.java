package com.github.sdmimaye.rpio.server.services.gpio.ctrl.dummy;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioTimeoutOutput;

public class DummyGpioTimeoutOuput extends DummyGpioOutput implements GpioTimeoutOutput {
    private final int timeout;

    public DummyGpioTimeoutOuput(String description, int address, GpioPinStateListener listener, int timeout) {
        super(description, address, listener);
        this.timeout = timeout;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }
}

