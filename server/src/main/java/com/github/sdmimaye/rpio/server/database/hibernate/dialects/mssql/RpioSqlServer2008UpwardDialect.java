package com.github.sdmimaye.rpio.server.database.hibernate.dialects.mssql;

import org.hibernate.dialect.SQLServer2008Dialect;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

public class RpioSqlServer2008UpwardDialect extends SQLServer2008Dialect {
    public RpioSqlServer2008UpwardDialect() {
        super();
        registerColumnType(Types.BIGINT, "bigint");

        registerColumnType(Types.CHAR, "nchar(1)");
        registerColumnType(Types.LONGVARCHAR, "nvarchar(max)" );
        registerColumnType(Types.VARCHAR, 4000, "nvarchar($l)");
        registerColumnType(Types.VARCHAR, "nvarchar(max)");
        registerColumnType(Types.CLOB, "nvarchar(max)" );

        registerColumnType(Types.NCHAR, "nchar(1)");
        registerColumnType(Types.LONGNVARCHAR, "nvarchar(max)");
        registerColumnType(Types.NVARCHAR, 4000, "nvarchar($l)");
        registerColumnType(Types.NVARCHAR, "nvarchar(max)");
        registerColumnType(Types.NCLOB, "nvarchar(max)");

        registerHibernateType(Types.NCHAR, StandardBasicTypes.CHARACTER.getName());
        registerHibernateType(Types.LONGNVARCHAR, StandardBasicTypes.TEXT.getName());
        registerHibernateType(Types.NVARCHAR, StandardBasicTypes.STRING.getName());
        registerHibernateType(Types.NCLOB, StandardBasicTypes.CLOB.getName() );

    }

    @Override
    public boolean bindLimitParametersInReverseOrder() {
        return true;
    }
}
