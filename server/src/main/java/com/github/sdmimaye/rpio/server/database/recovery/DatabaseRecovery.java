package com.github.sdmimaye.rpio.server.database.recovery;

import com.github.sdmimaye.rpio.common.config.core.Configuration;
import com.github.sdmimaye.rpio.server.config.DatabaseConfig;
import com.github.sdmimaye.rpio.server.database.models.enums.DbType;
import com.github.sdmimaye.rpio.server.database.recovery.hsql.HsqlDatabaseBackupGenerator;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.io.File;
import java.io.InputStream;

public class DatabaseRecovery {
    private final Injector injector;

    @Inject
    public DatabaseRecovery(Injector injector) {
        this.injector = injector;
    }

    private DatabaseBackupGenerator generator(Configuration configuration){
        DatabaseConfig config = configuration.read(DatabaseConfig.class);
        DbType dbtype = config.getType();
        switch (dbtype) {
            case Local:
                return injector.getInstance(HsqlDatabaseBackupGenerator.class);
        }

        throw new RuntimeException("Invalid Database type for Backup creation: " + dbtype);
    }

    public File backup(String request) {
        Configuration configuration = injector.getInstance(Configuration.class);
        DatabaseBackupGenerator generator = generator(configuration);
        DatabaseConfig config = configuration.read(DatabaseConfig.class);

        String host = config.getServer();
        String database = config.getDatabase();
        String username = config.getUser();
        String password = config.getPassword();
        return generator.getBackup(host, database, username, password, request);
    }

    public String backup() {
        Configuration configuration = injector.getInstance(Configuration.class);
        DatabaseBackupGenerator generator = generator(configuration);
        DatabaseConfig config = configuration.read(DatabaseConfig.class);

        String host = config.getServer();
        String database = config.getDatabase();
        String username = config.getUser();
        String password = config.getPassword();
        String backupPassword = config.getPassword();
        return generator.createBackup(host, database, username, password, backupPassword);
    }

    public String restore(InputStream stream) {
        Configuration configuration = injector.getInstance(Configuration.class);
        DatabaseBackupGenerator generator = generator(configuration);
        DatabaseConfig config = configuration.read(DatabaseConfig.class);

        String host = config.getServer();
        String database = config.getDatabase();
        String username = config.getUser();
        String password = config.getPassword();
        return generator.setBackup(host, database, username, password, stream);
    }

    public void restore(String request){
        Configuration configuration = injector.getInstance(Configuration.class);
        DatabaseBackupGenerator generator = generator(configuration);
        DatabaseConfig config = configuration.read(DatabaseConfig.class);

        String host = config.getServer();
        String database = config.getDatabase();
        String username = config.getUser();
        String password = config.getPassword();
        String backupPassword = config.getPassword();

        generator.restore(host, database, username, password, request, backupPassword);
    }
}
