package com.github.sdmimaye.rpio.server.services.gpio;

import com.github.sdmimaye.rpio.common.services.RpioService;
import com.github.sdmimaye.rpio.server.services.gpio.ctrl.HardwareController;
import com.github.sdmimaye.rpio.server.services.gpio.ctrl.LinuxHardwareController;
import com.github.sdmimaye.rpio.server.services.gpio.ctrl.WindowsHardwareController;
import com.github.sdmimaye.rpio.server.util.os.OsDependantInjector;
import com.google.inject.Inject;

public class GpioService implements RpioService {
    private final OsDependantInjector injector;

    @Inject
    public GpioService(OsDependantInjector injector) {
        this.injector = injector.register(HardwareController.class, WindowsHardwareController.class, LinuxHardwareController.class);
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void run() {

    }
}
