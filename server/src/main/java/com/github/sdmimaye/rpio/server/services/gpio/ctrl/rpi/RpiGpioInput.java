package com.github.sdmimaye.rpio.server.services.gpio.ctrl.rpi;

import com.github.sdmimaye.rpio.server.database.models.enums.PinLogic;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.io.IOException;

public class RpiGpioInput implements GpioInput{
    private final GpioPinDigitalInput pin;
    private final GpioController gpio;
    private final PinLogic logic;

    public RpiGpioInput(GpioController gpio, int number, PinLogic logic, String description, GpioPinStateListener listener) {
        this.gpio = gpio;
        this.logic = logic;
        this.pin = gpio.provisionDigitalInputPin(RaspiPin.getPinByAddress(number), description);
        this.pin.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                listener.onPinStateChanged(getDescription(), getNumber(), GpioPinState.byValue(event.getState(), logic));
            }
        });
    }

    @Override
    public void close() throws IOException {
        pin.removeAllListeners();
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
        return GpioPinState.byValue(pin.getState(), logic);
    }
}
