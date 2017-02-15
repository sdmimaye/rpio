package com.github.sdmimaye.rpio.server.services.gpio.classes;

import java.util.Random;

public enum GpioPinState {
    LOW,
    HIGH;

    public static GpioPinState getRandomState(){
        GpioPinState[] all = GpioPinState.values();
        return all[new Random().nextInt(all.length)];
    }
}
