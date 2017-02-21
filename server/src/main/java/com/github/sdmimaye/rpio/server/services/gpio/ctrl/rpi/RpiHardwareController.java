package com.github.sdmimaye.rpio.server.services.gpio.ctrl.rpi;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.ctrl.HardwareController;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;
import com.google.inject.Singleton;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiPin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RpiHardwareController implements HardwareController {
    private static final Logger logger = LoggerFactory.getLogger(RpiHardwareController.class);

    private final GpioController gpio;

    public RpiHardwareController() {
        logger.info("Building {}...", getClass().getSimpleName());
        this.gpio = GpioFactory.getInstance();
    }

    @Override
    public GpioInput getInputPin(String description, int address, GpioPinStateListener listener) {
        return new RpiGpioInput(gpio.provisionDigitalInputPin(RaspiPin.getPinByAddress(address), description), listener);
    }

    @Override
    public GpioOutput getOutputPin(String description, int address, GpioPinStateListener listener) {
        return new RpiGpioOuput(gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(address), description), listener);
    }
}
