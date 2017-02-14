package com.github.sdmimaye.rpio.server;

import com.github.sdmimaye.rpio.common.config.ConfigModule;
import com.github.sdmimaye.rpio.common.security.keystore.CertificateInfoProvider;
import com.github.sdmimaye.rpio.server.database.DatabaseModule;
import com.github.sdmimaye.rpio.server.services.ServiceModule;
import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CertificateInfoProvider.class).toInstance(new CertificateInfoProvider() {
            @Override
            public String getCommonName() {
                return "Rpio-Server";
            }

            @Override
            public int getValidDays() {
                return 365 * 10;//10 years
            }
        });
        install(new DatabaseModule());
        install(new ServiceModule());
        install(new ConfigModule());
    }
}
