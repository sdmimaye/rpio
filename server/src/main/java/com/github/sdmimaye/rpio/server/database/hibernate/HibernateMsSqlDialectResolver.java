package com.github.sdmimaye.rpio.server.database.hibernate;

import com.github.sdmimaye.rpio.server.database.hibernate.dialects.mssql.RpioSqlServer2005Dialect;
import com.github.sdmimaye.rpio.server.database.hibernate.dialects.mssql.RpioSqlServer2008UpwardDialect;
import com.github.sdmimaye.rpio.server.database.hibernate.dialects.mssql.RpioSqlServerFallbackDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateMsSqlDialectResolver implements DialectResolver {
    private static final Logger logger = LoggerFactory.getLogger(HibernateMsSqlDialectResolver.class);

    @Override
    public Dialect resolveDialect(DialectResolutionInfo info) {
        int databaseMajorVersion = info.getDatabaseMajorVersion();
        switch (databaseMajorVersion) {
            case 8:
                return new RpioSqlServerFallbackDialect();
            case 9:
                return new RpioSqlServer2005Dialect();
            case 10:
            case 11:
            case 12:
                return new RpioSqlServer2008UpwardDialect();
            default:
                logger.warn("Unknown Microsoft SQL server version {}! Falling back to {} 2000 dialect.", databaseMajorVersion, RpioSqlServerFallbackDialect.class.getSimpleName());
                return new RpioSqlServerFallbackDialect();
        }
    }
}