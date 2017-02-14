package com.github.sdmimaye.rpio.server.database;

import com.github.sdmimaye.rpio.common.config.core.Configuration;
import com.github.sdmimaye.rpio.server.config.DatabaseConfig;
import com.github.sdmimaye.rpio.server.database.models.enums.DbType;
import com.github.sdmimaye.rpio.server.http.rest.models.json.config.JsonDatabaseConfig;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTester {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseTester.class);
    private final Configuration configuration;

    @Inject
    public DatabaseTester(Configuration configuration) {
        this.configuration = configuration;
    }

    public static boolean testDatabase(DbType type, String host, String database, String username, String password) {
        String url = type.build(host, database);
        password = JsonDatabaseConfig.PASSWORD_PLACEHOLDER.equals(password) ? null : password;

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            return true;
        } catch (SQLException e) {
            logger.warn("Could not connect to database on url:" + url);
            return false;
        } finally {
            try{
                if(connection != null)
                    connection.close();
            }catch (SQLException e) {
                logger.error("Error while closing connecting to database", e);
            }finally {
                connection = null;
            }
        }
    }

    public boolean testCurrentDatabaseConfiguration() {
        DatabaseConfig config = configuration.read(DatabaseConfig.class);
        DbType dbtype = config.getType();
        String host = config.getServer();
        String database = config.getDatabase();
        String username = config.getUser();
        String password = config.getPassword();

        return DatabaseTester.testDatabase(dbtype, host, database, username, password);
    }

}
