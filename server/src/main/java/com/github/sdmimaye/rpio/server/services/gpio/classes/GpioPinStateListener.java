package com.github.sdmimaye.rpio.server.services.gpio.classes;

public interface GpioPinStateListener {
    void onPinStateChanged(String description, int number, GpioPinState state);
}
