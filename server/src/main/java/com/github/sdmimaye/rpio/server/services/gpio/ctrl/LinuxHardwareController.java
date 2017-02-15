package com.github.sdmimaye.rpio.server.services.gpio.ctrl;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;
import com.google.inject.Singleton;
import com.pi4j.io.gpio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LinuxHardwareController implements HardwareController{
    private static final Logger logger = LoggerFactory.getLogger(LinuxHardwareController.class);

    private final GpioController gpio;

    public LinuxHardwareController() {
        logger.info("Building {}...", getClass().getSimpleName());
        this.gpio = GpioFactory.getInstance();
    }

    @Override
    public GpioInput getInputPin(int address) {
        return new GpioInput() {
            private final GpioPinDigitalInput pin = gpio.provisionDigitalInputPin(RaspiPin.getPinByAddress(address));

            @Override
            public GpioPinState getState() {
                PinState state = pin.getState();
                switch (state) {
                    case HIGH:
                        return GpioPinState.HIGH;
                    case LOW:
                        return GpioPinState.LOW;
                    default:
                        throw new RuntimeException("Invalid GPIO-State detected: " + state);
                }
            }
        };
    }

    @Override
    public GpioOutput getOutputPin(int address) {
        return new GpioOutput() {
            private final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(address));

            @Override
            public void setState(GpioPinState state) {
                switch (state) {
                    case HIGH:
                        pin.setState(PinState.HIGH);
                        break;
                    case LOW:
                        pin.setState(PinState.LOW);
                        break;
                }
            }
        };
    }
}
