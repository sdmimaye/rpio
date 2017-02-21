package com.github.sdmimaye.rpio.server.services.gpio.ctrl.dummy;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.ctrl.HardwareController;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DummyHardwareController implements HardwareController {
    private static final Logger logger = LoggerFactory.getLogger(DummyHardwareController.class);

    public DummyHardwareController() {
        logger.info("Building {}...", getClass().getSimpleName());
    }

    @Override
    public GpioOutput getOutputPin(String description, int address, GpioPinStateListener listener) {
        return new DummyGpioOutput(description, address, listener);
    }

    @Override
    public GpioInput getInputPin(String description, int address, GpioPinStateListener listener) {
        return new DummyGpioInput(description, address, listener);
    }
}
