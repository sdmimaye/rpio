package com.github.sdmimaye.rpio.server.services.gpio.ctrl;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;
import com.google.inject.Singleton;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
            public void close() throws IOException {
                pin.removeAllListeners();
            }

            @Override
            public int getNumber() {
                return pin.getPin().getAddress();
            }

            @Override
            public void register(GpioPinStateListener listener) {
                pin.addListener(new GpioPinListenerDigital() {
                    @Override
                    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                        listener.onPinStateChanged(getNumber(), GpioPinState.byValue(event.getState()));
                    }
                });
            }

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
            public void close() throws IOException {
                pin.removeAllListeners();
            }

            @Override
            public int getNumber() {
                return pin.getPin().getAddress();
            }

            @Override
            public void setState(GpioPinState state) {
                pin.setState(state.toValue());
            }
        };
    }
}
