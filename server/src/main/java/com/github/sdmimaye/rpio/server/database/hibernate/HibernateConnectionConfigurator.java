package com.github.sdmimaye.rpio.server.database.hibernate;

import com.github.sdmimaye.rpio.server.config.DatabaseConfig;
import com.github.sdmimaye.rpio.server.database.models.enums.DbType;
import com.google.inject.Inject;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.envers.configuration.EnversSettings;
import org.hsqldb.jdbcDriver;

import java.io.IOException;

public class HibernateConnectionConfigurator {
    public static final String TABLE_NAME_PREFIX = "RPIO_";

    private final com.github.sdmimaye.rpio.common.config.core.Configuration config;

    @Inject
    public HibernateConnectionConfigurator(com.github.sdmimaye.rpio.common.config.core.Configuration config) {
        this.config = config;
    }

    public void configure(Configuration configuration) {
        DatabaseConfig config = this.config.read(DatabaseConfig.class);

        configureCredentials(configuration, config);
        configureDriver(configuration);
        configureUrl(configuration, config);
        configureNamingStrategy(configuration);
        configureEnvers(configuration);
        configureDialectResolver(configuration, config);
    }

    public void resetToFailsafeConfiguration() throws IOException {
        DatabaseConfig config = this.config.read(DatabaseConfig.class);
        config.reset();
        this.config.write(config);
    }

    private void configureNamingStrategy(Configuration configuration) {
        configuration.setNamingStrategy(new PrefixNamingStrategy(TABLE_NAME_PREFIX));
    }

    private void configureCredentials(Configuration configuration, DatabaseConfig config) {
        configuration.setProperty(AvailableSettings.USER, config.getUser());
        configuration.setProperty(AvailableSettings.PASS, config.getPassword());
    }

    private void configureUrl(Configuration configuration, DatabaseConfig config) {
        DbType type = config.getType();
        String url = type.build(config.getServer(), config.getDatabase());
        configuration.setProperty(AvailableSettings.URL, url);
    }

    private void configureDriver(Configuration configuration) {
        configuration.setProperty(AvailableSettings.DRIVER, jdbcDriver.class.getName());
    }

    private void configureEnvers(Configuration configuration) {
        configuration.setProperty(EnversSettings.AUDIT_TABLE_PREFIX, HibernateConnectionConfigurator.TABLE_NAME_PREFIX + "_AUD_");
        configuration.setProperty(EnversSettings.AUDIT_TABLE_SUFFIX, "");
    }

    private void configureDialectResolver(Configuration configuration, DatabaseConfig config){
        DbType type = config.getType();
        if(type == DbType.MsSql)
            configuration.setProperty(AvailableSettings.DIALECT_RESOLVERS, HibernateMsSqlDialectResolver.class.getName());
    }
}
