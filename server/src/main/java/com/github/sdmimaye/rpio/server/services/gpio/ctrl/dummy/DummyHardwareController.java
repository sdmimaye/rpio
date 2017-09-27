package com.github.sdmimaye.rpio.server.services.gpio.ctrl.dummy;

import com.github.sdmimaye.rpio.server.database.models.enums.PinLogic;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.ctrl.HardwareController;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioTimeoutOutput;
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
    public GpioOutput getOutputPin(String description, int address, PinLogic logic, GpioPinStateListener listener) {
        return new DummyGpioOutput(description, address, logic, listener);
    }

    @Override
    public GpioTimeoutOutput getTimeoutOutputPin(String descirption, int address, PinLogic logic,GpioPinStateListener listener, int timeout) {
        return new DummyGpioTimeoutOuput(descirption, address, logic, listener, timeout);
    }

    @Override
    public GpioInput getInputPin(String description, int address, PinLogic logic,GpioPinStateListener listener) {
        return new DummyGpioInput(description, address, logic, listener);
    }
}
