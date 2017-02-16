package com.github.sdmimaye.rpio.server.http.rest.models.json.gpio;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;

public class JsonPinState {
    private int number;
    private GpioPinState state;

    public JsonPinState() {
    }

    public JsonPinState(int number, GpioPinState state) {
        this.number = number;
        this.state = state;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public GpioPinState getState() {
        return state;
    }

    public void setState(GpioPinState state) {
        this.state = state;
    }
}
