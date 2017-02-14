package com.github.sdmimaye.rpio.common.services;

import java.util.List;

public interface RpioServiceList {
    List<Class<? extends RpioService>> getAllServiceTypes();
}
