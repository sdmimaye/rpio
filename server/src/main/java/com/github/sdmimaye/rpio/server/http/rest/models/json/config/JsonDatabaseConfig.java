package com.github.sdmimaye.rpio.server.http.rest.models.json.config;

import com.github.sdmimaye.rpio.common.config.core.Configuration;
import com.github.sdmimaye.rpio.server.config.DatabaseConfig;
import com.github.sdmimaye.rpio.server.database.models.enums.DbType;

public class JsonDatabaseConfig {
    public static final String PASSWORD_PLACEHOLDER = "' OR '1' = '1";

    private DbType type;
    private String server;
    private String name;
    private String username;
    private String password;
    private String adminPassword;

    public JsonDatabaseConfig() {

    }

    public JsonDatabaseConfig(Configuration configuration) {
        DatabaseConfig database = configuration.read(DatabaseConfig.class);

        this.server = database.getServer();
        this.name = database.getDatabase();
        this.username = database.getUser();
        this.password = PASSWORD_PLACEHOLDER;
        this.type = database.getType();
    }

    public DbType getType() {
        return type;
    }

    public void setType(DbType type) {
        this.type = type;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
