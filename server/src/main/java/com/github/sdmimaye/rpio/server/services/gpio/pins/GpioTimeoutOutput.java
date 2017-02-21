package com.github.sdmimaye.rpio.server.services.gpio.pins;

import com.github.sdmimaye.rpio.common.utils.threads.ThreadUtils;
import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;

public interface GpioTimeoutOutput extends GpioOutput {
    void setState(GpioPinState state);
    int getTimeout();

    @Override
    default PinMode getPinMode() {
        return PinMode.OUTPUT;
    }
    default void change(){
        new Thread(() -> {
            synchronized (this) {
                setState(GpioPinState.HIGH);
                ThreadUtils.sleep(getTimeout());
                setState(GpioPinState.LOW);
            }
        }, "GIO-Timeout").start();
    }
}
