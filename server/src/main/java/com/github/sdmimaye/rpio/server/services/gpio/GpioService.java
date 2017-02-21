package com.github.sdmimaye.rpio.server.services.gpio;

import com.github.sdmimaye.rpio.common.services.RpioService;
import com.github.sdmimaye.rpio.common.utils.threads.ThreadUtils;
import com.github.sdmimaye.rpio.server.database.dao.gpio.GpioPinDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import com.github.sdmimaye.rpio.server.database.models.gpio.GpioPin;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.ctrl.HardwareController;
import com.github.sdmimaye.rpio.server.services.gpio.pins.Gpio;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioOutput;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Singleton
public class GpioService implements RpioService, GpioPinStateListener {
    private static final Logger logger = LoggerFactory.getLogger(GpioService.class);

    private final HardwareController controller;
    private final Object mutex = new Object();

    private final List<GpioPinStateListener> listeners = new ArrayList<>();
    private final List<Gpio> gpios = new ArrayList<>();

    private final GpioPinDao dao;
    private final HibernateUtil util;

    @Inject
    public GpioService(HardwareController controller, GpioPinDao dao, HibernateUtil util) {
        this.controller = controller;
        this.dao = dao;
        this.util = util;
    }

    public void register(GpioPinStateListener listener) {
        synchronized (mutex) {
            listeners.add(listener);
            gpios.forEach(g -> broadcast(listener, g.getDescription(), g.getNumber(), g.getState()));
        }
    }

    public void write(int number, GpioPinState state) {
        synchronized (mutex) {
            gpios.stream()
                    .filter(g -> g.getPinMode() != PinMode.OUTPUT && g.getNumber() == number)
                    .map(g -> (GpioOutput) g)
                    .forEach(p -> p.setState(state));
        }
    }

    private void broadcast(GpioPinStateListener listener, String description, int number, GpioPinState state) {
        synchronized (mutex) {
            listener.onPinStateChanged(description, number, state);
        }
    }

    private void broadcast(String description, int number, GpioPinState state) {
        synchronized (mutex) {
            listeners.forEach(l -> broadcast(l, description, number, state));
        }
    }

    public void broadcast() {
        synchronized (mutex) {
            gpios.forEach(g -> broadcast(g.getDescription(), g.getNumber(), g.getState()));
        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void run() {
        do {
            sync();
        } while (ThreadUtils.sleep(5000));
    }

    public void sync() {
        logger.trace("Synchronizing GPIO-Pins...");
        util.doWork(() -> {
            synchronized (mutex) {
                List<GpioPin> pins = dao.getAll();

                logger.trace("Removing deleted Pins...");
                doRemoveMissingPins(pins);

                logger.trace("Adding missing Pins...");
                doAddMissingPins(pins);

                broadcast();
            }
        });
    }

    private void doRemoveMissingPins(List<GpioPin> pins) {
        Iterator<Gpio> iterator = gpios.iterator();
        while (iterator.hasNext()) {
            Gpio next = iterator.next();
            if(pins.stream().anyMatch(p -> p.getMode() == next.getPinMode() && p.getNumber() == next.getNumber()))
                continue;

            IOUtils.closeQuietly(next);
            iterator.remove();
        }
    }

    private void doAddMissingPins(List<GpioPin> pins) {
        for (GpioPin pin : pins) {
            if(gpios.stream().anyMatch(g -> g.getNumber() == pin.getNumber()))
                continue;

            switch (pin.getMode()) {
                case INPUT:
                    gpios.add(controller.getInputPin(pin.getDescription(), pin.getNumber(), this));
                    break;
                case OUTPUT:
                    switch (pin.getOuputMode()) {
                        case TOGGLE:
                            gpios.add(controller.getOutputPin(pin.getDescription(), pin.getNumber(), this));
                            break;
                        case TIMEOUT:
                            gpios.add(controller.getTimeoutOutputPin(pin.getDescription(), pin.getNumber(), this, pin.getTimeout()));
                            break;
                    }
                    break;
                default:
                    throw new RuntimeException("Invalid Mode for Pin-Number: " + pin.getNumber());
            }
        }
    }

    public void change(String number) {
        try {
            int pin = Integer.parseInt(number);
            synchronized (mutex) {
                gpios.stream()
                        .filter(g -> g.getPinMode() == PinMode.OUTPUT && g.getNumber() == pin)
                        .map(g -> (GpioOutput)g)
                        .forEach(GpioOutput::change);
                broadcast();
            }
        } catch (Exception ex) {
            logger.warn("Could not set GPIO with invalid Pin-Number: " + number);
        }
    }

    @Override
    public void onPinStateChanged(String description, int number, GpioPinState state) {
        broadcast(description, number, state);
    }
}
