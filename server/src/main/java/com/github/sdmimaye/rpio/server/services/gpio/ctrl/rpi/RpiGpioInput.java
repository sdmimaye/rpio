package com.github.sdmimaye.rpio.server.services.gpio.ctrl.rpi;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.io.IOException;

public class RpiGpioInput implements GpioInput{
    private final GpioPinDigitalInput pin;

    public RpiGpioInput(GpioPinDigitalInput pin, GpioPinStateListener listener) {
        this.pin = pin;
        this.pin.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                listener.onPinStateChanged(getDescription(), getNumber(), GpioPinState.byValue(event.getState()));
            }
        });
    }

    @Override
    public void close() throws IOException {
        pin.removeAllListeners();
    }

    @Override
    public int getNumber() {
        return pin.getPin().getAddress();
    }

    @Override
    public String getDescription() {
        return pin.getName();
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
}
