package com.github.sdmimaye.rpio.server.services.gpio.ctrl.dummy;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DummyGpioOutput implements GpioOutput{
    private static final Logger logger = LoggerFactory.getLogger(DummyGpioOutput.class);

    private final String description;
    private final int address;
    private final GpioPinStateListener listener;
    private GpioPinState state = GpioPinState.LOW;

    public DummyGpioOutput(String description, int address, GpioPinStateListener listener) {
        this.description = description;
        this.address = address;
        this.listener = listener;
    }

    @Override
    public void setState(GpioPinState state) {
        this.state = state;
        logger.info("Dummy-Output Pin: {} set to: {}", address, state);

        listener.onPinStateChanged(description, address, state);
    }

    @Override
    public int getNumber() {
        return address;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public GpioPinState getState() {
        return state;
    }

    @Override
    public void close() throws IOException {
        logger.info("Dummy-Output Pin: {} closed", address);
    }
}