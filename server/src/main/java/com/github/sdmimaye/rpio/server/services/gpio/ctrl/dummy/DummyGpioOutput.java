package com.github.sdmimaye.rpio.server.services.gpio.ctrl.dummy;

import com.github.sdmimaye.rpio.server.database.models.enums.PinLogic;
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
    private final PinLogic logic;
    private final GpioPinStateListener listener;
    private GpioPinState state;

    public DummyGpioOutput(String description, int address, PinLogic logic, GpioPinStateListener listener) {
        this.description = description;
        this.address = address;
        this.logic = logic;
        this.listener = listener;

        this.state = logic == PinLogic.NORMAL ? GpioPinState.LOW : GpioPinState.HIGH;
    }

    @Override
    public void setState(GpioPinState state) {
        switch (logic) {
            case NORMAL:
                this.state = state;
                break;
            case INVERTED:
                switch (state) {
                    case HIGH:
                        this.state = GpioPinState.LOW;
                        break;
                    case LOW:
                        this.state = GpioPinState.HIGH;
                        break;
                }
        }
        logger.info("Dummy-Output Pin: {} set to: {}", address, this.state);
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