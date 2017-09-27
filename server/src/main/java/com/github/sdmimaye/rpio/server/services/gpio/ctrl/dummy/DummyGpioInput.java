package com.github.sdmimaye.rpio.server.services.gpio.ctrl.dummy;

import com.github.sdmimaye.rpio.common.utils.threads.ThreadUtils;
import com.github.sdmimaye.rpio.server.database.models.enums.PinLogic;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DummyGpioInput implements GpioInput {
    private static final Logger logger = LoggerFactory.getLogger(DummyGpioInput.class);

    private final String description;
    private final int address;
    private final PinLogic logic;
    private final GpioPinStateListener listener;
    private final Thread worker;

    private GpioPinState state = GpioPinState.LOW;

    public DummyGpioInput(String description, int address, PinLogic logic, GpioPinStateListener listener) {
        this.description = description;
        this.address = address;
        this.logic = logic;
        this.listener = listener;

        this.worker = new Thread(() ->{
            do {
                GpioPinState update = GpioPinState.getRandomState();
                if(this.state == update) continue;

                this.state = update;
                this.listener.onPinStateChanged(description, address, state);
            } while (ThreadUtils.sleep(1000));
        }, "DGIO: " + description);
        this.worker.setDaemon(true);
        this.worker.start();
    }

    @Override
    public GpioPinState getState() {
        logger.info("GPIO {} get-state: {}", address, state);
        return state;
    }

    @Override
    public int getNumber() {
        return address;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void close() throws IOException {
        logger.info("Dummy-Input Pin: {} closed: {}", address);
        this.worker.interrupt();
    }
}
