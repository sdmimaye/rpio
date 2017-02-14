package com.github.sdmimaye.rpio.common.config.core;

public interface Configuration {
    <T extends Config> T read(Class<T> type);
    <T extends Config> void write(T value);
}
