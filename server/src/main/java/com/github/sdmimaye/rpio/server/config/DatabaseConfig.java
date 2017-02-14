package com.github.sdmimaye.rpio.server.config;

import com.github.sdmimaye.rpio.common.config.core.Config;
import com.github.sdmimaye.rpio.server.database.models.enums.DbType;

public class DatabaseConfig implements Config{
    private String user;
    private String password;
    private DbType type;
    private String server;
    private String database;
    private boolean initialized;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public DbType getType() {
        return type;
    }

    public void setType(DbType type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public void reset() {
        type = DbType.Local;
        server = "./config/database/";
        database = "rpio.db";
        user = "sa";
        password = "";
        initialized = false;
    }
}
