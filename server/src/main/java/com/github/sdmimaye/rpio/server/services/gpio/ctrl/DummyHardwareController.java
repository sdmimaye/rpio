package com.github.sdmimaye.rpio.server.services.gpio.ctrl;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Singleton
public class DummyHardwareController implements HardwareController {
    private static final Logger logger = LoggerFactory.getLogger(DummyHardwareController.class);

    public DummyHardwareController() {
        logger.info("Building {}...", getClass().getSimpleName());
    }

    @Override
    public GpioOutput getOutputPin(int address) {
        return new GpioOutput() {
            @Override
            public void setState(GpioPinState state) {
                logger.info("Dummy-Output Pin: {} set to: {}", address, state);
            }

            @Override
            public int getNumber() {
                return address;
            }

            @Override
            public void close() throws IOException {
                logger.info("Dummy-Output Pin: {} closed", address);
            }
        };
    }

    @Override
    public GpioInput getInputPin(int address) {
        return new GpioInput() {
            @Override
            public void register(GpioPinStateListener listener) {
                logger.info("Dummy-Input Pin: {} registered: {}", address);
            }

            @Override
            public GpioPinState getState() {
                GpioPinState state = GpioPinState.getRandomState();
                logger.info("Dummy-Input Pin: {} get-state: {}", address, state);
                return state;
            }

            @Override
            public int getNumber() {
                return address;
            }

            @Override
            public void close() throws IOException {
                logger.info("Dummy-Input Pin: {} closed: {}", address);
            }
        };
    }
}
