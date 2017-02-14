package com.github.sdmimaye.rpio.common.config;

import com.github.sdmimaye.rpio.common.config.core.Configuration;
import com.github.sdmimaye.rpio.common.config.impls.JsonConfiguration;
import com.google.inject.AbstractModule;

public class ConfigModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Configuration.class).to(JsonConfiguration.class);
    }
}