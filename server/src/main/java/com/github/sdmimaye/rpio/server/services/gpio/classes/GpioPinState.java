package com.github.sdmimaye.rpio.server.services.gpio.classes;

import com.pi4j.io.gpio.PinState;

import java.util.Random;

public enum GpioPinState {
    LOW,
    HIGH;

    public static GpioPinState getRandomState(){
        GpioPinState[] all = GpioPinState.values();
        return all[new Random().nextInt(all.length)];
    }

    public static GpioPinState byValue(PinState state) {
        switch (state) {
            case HIGH:
                return HIGH;
            case LOW:
                return LOW;
            default:
                throw new RuntimeException("Invalid GPIO Pin state: " + state);
        }
    }

    public PinState toValue(){
        switch (this) {
            case HIGH:
                return PinState.HIGH;
            case LOW:
                return PinState.LOW;
            default:
                throw new RuntimeException("Invalid GPIO Pin state: " + this);
        }
    }
}
