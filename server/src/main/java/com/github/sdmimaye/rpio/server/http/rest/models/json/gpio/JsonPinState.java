package com.github.sdmimaye.rpio.server.http.rest.models.json.gpio;

import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;

public class JsonPinState {
    private String description;
    private int number;
    private GpioPinState state;

    public JsonPinState(String description, int number, GpioPinState state) {
        this.description = description;
        this.number = number;
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
