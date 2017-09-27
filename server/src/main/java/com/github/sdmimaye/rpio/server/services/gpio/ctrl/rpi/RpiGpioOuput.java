package com.github.sdmimaye.rpio.server.services.gpio.ctrl.rpi;

import com.github.sdmimaye.rpio.server.database.models.enums.PinLogic;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.io.IOException;

public class RpiGpioOuput implements GpioOutput {
    private final GpioPinDigitalOutput pin;
    private final GpioController gpio;
    private final PinLogic logic;

    public RpiGpioOuput(GpioController gpio, int number, PinLogic logic, String description, GpioPinStateListener listener) {
        this.gpio = gpio;
        this.logic = logic;
        this.pin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(number), description, logic == PinLogic.NORMAL ? PinState.LOW : PinState.HIGH);
        this.pin.addListener((GpioPinListenerDigital) event -> listener.onPinStateChanged(getDescription(), getNumber(), GpioPinState.byValue(event.getState(), logic)));
    }

    @Override
    public void close() throws IOException {
        pin.removeAllListeners();
        pin.setState(logic == PinLogic.NORMAL ? PinState.LOW : PinState.HIGH);
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

    @Override
    public void setState(GpioPinState state) {
        pin.setState(state.toValue(logic));
    }
}

