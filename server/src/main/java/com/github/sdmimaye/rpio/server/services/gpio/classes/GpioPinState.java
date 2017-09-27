package com.github.sdmimaye.rpio.server.services.gpio.classes;

import com.github.sdmimaye.rpio.server.database.models.enums.PinLogic;
import com.pi4j.io.gpio.PinState;

import java.util.Random;

public enum GpioPinState {
    LOW,
    HIGH;

    public static GpioPinState getRandomState(){
        GpioPinState[] all = GpioPinState.values();
        return all[new Random().nextInt(all.length)];
    }

    public static GpioPinState byValue(PinState state, PinLogic logic) {
        return logic == PinLogic.NORMAL ?
                state == PinState.LOW ? GpioPinState.LOW : GpioPinState.HIGH :
                state == PinState.LOW ? GpioPinState.HIGH : GpioPinState.LOW;
    }

    public PinState toValue(PinLogic logic){
        return logic == PinLogic.NORMAL ?
                this == HIGH ? PinState.HIGH : PinState.LOW :
                this == HIGH ? PinState.LOW : PinState.HIGH;
    }
}
