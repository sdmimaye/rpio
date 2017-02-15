package com.github.sdmimaye.rpio.server.services.gpio.classes;

public interface GpioPinStateListener {
    void onPinStateChanged(int number, GpioPinState state);
}
