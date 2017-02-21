package com.github.sdmimaye.rpio.server.services.gpio;

import com.github.sdmimaye.rpio.common.utils.state.OsUtil;
import com.github.sdmimaye.rpio.server.services.gpio.ctrl.dummy.DummyHardwareController;
import com.github.sdmimaye.rpio.server.services.gpio.ctrl.HardwareController;
import com.github.sdmimaye.rpio.server.services.gpio.ctrl.rpi.RpiHardwareController;
import com.google.inject.AbstractModule;

public class GpioModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HardwareController.class).to(OsUtil.isLinux() ? RpiHardwareController.class : DummyHardwareController.class);
    }
}
