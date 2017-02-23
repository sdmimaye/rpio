package com.github.sdmimaye.rpio.server.services.gpio.ctrl.rpi;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.io.IOException;

public class RpiGpioOuput implements GpioOutput {
    private final GpioPinDigitalOutput pin;
    private final GpioController gpio;

    public RpiGpioOuput(GpioController gpio, int number, String description, GpioPinStateListener listener) {
        this.gpio = gpio;
        this.pin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(number), description);
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
        pin.setState(PinState.LOW);
        gpio.unprovisionPin(pin);
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
        return GpioPinState.byValue(pin.getState());
    }

    @Override
    public void setState(GpioPinState state) {
        pin.setState(state.toValue());
    }
}

