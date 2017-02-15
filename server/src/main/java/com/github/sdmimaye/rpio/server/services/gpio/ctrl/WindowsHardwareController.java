package com.github.sdmimaye.rpio.server.services.gpio.ctrl;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class WindowsHardwareController implements HardwareController {
    private static final Logger logger = LoggerFactory.getLogger(WindowsHardwareController.class);

    public WindowsHardwareController() {
        logger.info("Building {}...", getClass().getSimpleName());
    }

    @Override
    public GpioOutput getOutputPin(int address) {
        return state -> logger.info("Dummy-Output Pin: {} set to: {}", address, state);
    }

    @Override
    public GpioInput getInputPin(int address) {
        return () -> {
            GpioPinState state = GpioPinState.getRandomState();
            logger.info("Dummy-Input Pin: {} randomized to to: {}", address, state);
            return state;
        };
    }
}
