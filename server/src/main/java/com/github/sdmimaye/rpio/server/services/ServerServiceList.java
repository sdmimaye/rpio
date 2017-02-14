package com.github.sdmimaye.rpio.server.services;

import com.github.sdmimaye.rpio.common.services.RpioService;
import com.github.sdmimaye.rpio.common.services.RpioServiceList;
import com.github.sdmimaye.rpio.server.services.gcm.GcmService;
import com.github.sdmimaye.rpio.server.services.notifications.NotificationService;

import java.util.Arrays;
import java.util.List;

public class ServerServiceList implements RpioServiceList {
    @Override
    public List<Class<? extends RpioService>> getAllServiceTypes() {
        return Arrays.asList(
                NotificationService.class,
                GcmService.class
        );
    }
}
