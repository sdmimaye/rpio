package com.github.sdmimaye.rpio.server.services.gpio;

import com.github.sdmimaye.rpio.common.services.RpioService;
import com.github.sdmimaye.rpio.common.utils.threads.ThreadUtils;
import com.github.sdmimaye.rpio.server.database.dao.gpio.PinDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import com.github.sdmimaye.rpio.server.database.models.gpio.Pin;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.github.sdmimaye.rpio.server.services.gpio.ctrl.HardwareController;
import com.github.sdmimaye.rpio.server.services.gpio.pins.Gpio;
import com.github.sdmimaye.rpio.server.services.gpio.pins.GpioInput;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Singleton
public class GpioService implements RpioService {
    private static final Logger logger = LoggerFactory.getLogger(GpioService.class);

    private final HardwareController controller;
    private final Object mutex = new Object();

    private final List<GpioPinStateListener> listeners = new ArrayList<>();
    private final List<Gpio> gpios = new ArrayList<>();

    private final PinDao dao;
    private final HibernateUtil util;

    @Inject
    public GpioService(HardwareController controller, PinDao dao, HibernateUtil util) {
        this.controller = controller;
        this.dao = dao;
        this.util = util;
    }

    public void register(GpioPinStateListener listener) {
        synchronized (mutex) {
            listeners.add(listener);
            gpios.stream()
                    .filter(g -> g.getPinMode() != PinMode.INPUT)
                    .map(g -> (GpioInput) g)
                    .forEach(g -> broadcast(listener, g.getNumber(), g.getState()));
        }
    }

    private void broadcast(GpioPinStateListener listener, int number, GpioPinState state) {
        synchronized (mutex) {
            listener.onPinStateChanged(number, state);
        }
    }

    private void broadcast(int number, GpioPinState state) {
        synchronized (mutex) {
            listeners.forEach(l -> broadcast(l, number, state));
        }
    }

    private void broadcast() {
        synchronized (mutex) {
            gpios.stream()
                    .filter(g -> g.getPinMode() != PinMode.INPUT)
                    .map(g -> (GpioInput) g)
                    .forEach(g -> broadcast(g.getNumber(), g.getState()));
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
                List<Pin> pins = dao.getAll();

                logger.trace("Removing deleted Pins...");
                boolean removed = doRemoveMissingPins(pins);

                logger.trace("Adding missing Pins...");
                boolean added = doAddMissingPins(pins);

                if(added || removed) broadcast();
            }
        });
    }

    private boolean doRemoveMissingPins(List<Pin> pins) {
        boolean anyModification = false;
        Iterator<Gpio> iterator = gpios.iterator();
        while (iterator.hasNext()) {
            Gpio next = iterator.next();
            if(pins.stream().anyMatch(p -> p.getMode() == next.getPinMode() && p.getNumber() == next.getNumber()))
                continue;

            IOUtils.closeQuietly(next);
            iterator.remove();
            anyModification = true;
        }

        return anyModification;
    }

    private boolean doAddMissingPins(List<Pin> pins) {
        boolean anyModification = false;
        for (Pin pin : pins) {
            if(gpios.stream().anyMatch(g -> g.getNumber() == pin.getNumber()))
                continue;

            switch (pin.getMode()) {
                case INPUT:
                    gpios.add(controller.getInputPin(pin.getNumber()));
                    break;
                case OUTPUT:
                    gpios.add(controller.getOutputPin(pin.getNumber()));
                    break;
                default:
                    throw new RuntimeException("Invalid Mode for Pin-Number: " + pin.getNumber());
            }
            anyModification = true;
        }

        return anyModification;
    }
}
