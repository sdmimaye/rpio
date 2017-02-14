package com.github.sdmimaye.rpio.server.database.hibernate;

import com.github.sdmimaye.rpio.server.database.DatabaseTester;
import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.c3p0.internal.C3P0ConnectionProvider;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HibernateSessionFactoryInstantiator {
    private static final Logger logger = LoggerFactory.getLogger(HibernateSessionFactoryInstantiator.class);

    private final HibernateConnectionConfigurator configurator;
    private final HibernateClassMapper classMapper;
    private final DatabaseTester databaseTester;

    @Inject
    public HibernateSessionFactoryInstantiator(HibernateConnectionConfigurator configurator,
                                               HibernateClassMapper classMapper, DatabaseTester databaseTester) {
        this.configurator = configurator;
        this.classMapper = classMapper;
        this.databaseTester = databaseTester;
    }

    private void configureTransactionFactory(Configuration configuration) {
        configuration.setProperty("hibernate.transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory");
    }

    private void configureSessionContext(Configuration configuration) {
        configuration.setProperty("hibernate.current_session_context_class", "thread");
    }

    private void configureSchemaUpdate(Configuration configuration) {
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
    }

    private void configureConnectionPool(Configuration configuration) {
        configuration.setProperty("hibernate.connection.provider_class", C3P0ConnectionProvider.class.getName());
        configuration.setProperty("hibernate.c3p0.testConnectionOnCheckout", "true");
        configuration.setProperty("hibernate.c3p0.min_size", "5");
        configuration.setProperty("hibernate.c3p0.max_size", "20");
        configuration.setProperty("hibernate.c3p0.max_statements", "0");

        configuration.setProperty("hibernate.c3p0.idle_test_period", "0");
        configuration.setProperty("hibernate.c3p0.maxIdleTimeExcessConnections", "3360");

        configuration.setProperty("hibernate.c3p0.debugUnreturnedConnectionStackTraces", "true");
        configuration.setProperty("hibernate.c3p0.unreturnedConnectionTimeout", "600");
    }

    private SessionFactory tryCreateSessionFactory(){
        try {
            Configuration configuration = new Configuration();

            configureSchemaUpdate(configuration);
            configureTransactionFactory(configuration);
            configureSessionContext(configuration);
            configureConnectionPool(configuration);

            configurator.configure(configuration);
            classMapper.configureMappings(configuration);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception ex) {
            logger.error("Error while connecting database", ex);
            return null;
        }
    }

    public SessionFactory getSessionFactory() {
        for (int i = 0; i < 10; i++) {
            boolean success = databaseTester.testCurrentDatabaseConfiguration();
            if(success)
                return tryCreateSessionFactory();

            try {
                logger.error("Could not connect to database (try no. " + (i + 1) + " of 10). Next retry will occur in 5 seconds...");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                logger.error("Error while waiting for reconnecting database");
            }
        }//after 10 attempts, nothing happend -> reset to failsafe configuration
        try {
            configurator.resetToFailsafeConfiguration();
        } catch (IOException e) {
            logger.error("Error while reseting configuration to failsafe fallback.", e);
        }
        return tryCreateSessionFactory();
    }
}
