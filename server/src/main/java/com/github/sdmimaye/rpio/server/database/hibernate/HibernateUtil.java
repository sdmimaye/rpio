package com.github.sdmimaye.rpio.server.database.hibernate;

import com.github.sdmimaye.rpio.common.config.core.Configuration;
import com.github.sdmimaye.rpio.server.config.DatabaseConfig;
import com.github.sdmimaye.rpio.server.database.models.validation.ValidationException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.concurrent.Callable;

@Singleton
public class HibernateUtil {
    private static final Logger logger = Logger.getLogger(HibernateUtil.class);

    private final HibernateSessionFactoryInstantiator sessionFactoryInstatiator;
    private final Configuration configuration;
    private final Object sessionFactoryInstatiatorLock = new Object();

    private SessionFactory sessionFactoryInstance;

    @Inject
    public HibernateUtil(HibernateSessionFactoryInstantiator sessionFactoryInstatiator, Configuration configuration) {
        this.sessionFactoryInstatiator = sessionFactoryInstatiator;
        this.configuration = configuration;
    }

    public boolean isDatabaseInitialized() {
        DatabaseConfig config = configuration.read(DatabaseConfig.class);
        return config.isInitialized();
    }

    public void closeSession() {
        try {
            closeCurrentSessionIfPresent();
        } catch (Exception exCl) {
            logger.error("Failed to close database session.");
            printTransactionError(exCl);
        }
    }

    private void closeCurrentSessionIfPresent() {
        if (isSessionOpen()) {
            getSessionFactory().getCurrentSession().close();
        }
    }

    public <T> T doWork(Callable<T> callable){
        boolean previousTransactionActive = isCurrentTransactionActive();
        if (!previousTransactionActive) {
            beginTransaction();
        }

        try {
            T result = callable.call();
            if (!previousTransactionActive) {
                commitAndClose();
            }

            return result;
        }catch (ValidationException ve){
            throw ve;//just rethrow the exception
        }
        catch (Exception e) {
            logger.error("An error occured while executing a hibernate callable", e);
            throw new RuntimeException(e);
        } finally {
            if (!previousTransactionActive) {
                closeSession();
            }
        }
    }

    public void doWork(final Runnable runnable) {
        doWork((HibernateAction<Void>) () -> {
            runnable.run();
            return null;
        });
    }

    public <T> T doWork(HibernateAction<T> action) {
        boolean previousTransactionActive = isCurrentTransactionActive();
        if (!previousTransactionActive) {
            beginTransaction();
        }

        try {
            T result = action.call();

            if (!previousTransactionActive) {
                commitAndClose();
            }

            return result;
        } finally {
            if (!previousTransactionActive) {
                closeSession();
            }
        }
    }

    public void commitAndClose() {
        try {
            if (!isSessionOpen()) {
                return;
            }

            if (isCurrentTransactionActive()) {
                commitCurrentTransaction();
            }
        } catch (HibernateException ex) {
            logger.warn("Transaction could not commit.");
            printTransactionError(ex);
            tryToRollbackCurrentTransaction();
            throw new RuntimeException(ex.getMessage());
        } finally {
            closeSession();
        }
    }

    private boolean isSessionOpen() {
        return getSessionFactory() != null && getSessionFactory().getCurrentSession() != null;
    }

    private void tryToRollbackCurrentTransaction() {
        if (getSession().getTransaction() != null) {
            try {
                logger.debug("Executing rollback.");
                getSession().getTransaction().rollback();
            } catch (HibernateException exRb) {
                logger.error("Rollback failed.");
                printTransactionError(exRb);
            }
        }
    }

    private void commitCurrentTransaction() {
        getSession().getTransaction().commit();
    }

    public void commitAndRestartCurrentTransaction() {
        commitCurrentTransaction();
        beginTransaction();
    }

    public Session getSession() throws HibernateException {
        return getSessionFactory().getCurrentSession();
    }

    private SessionFactory getSessionFactory() {
        synchronized (sessionFactoryInstatiatorLock) {
            if (sessionFactoryInstance == null) {
                sessionFactoryInstance = sessionFactoryInstatiator.getSessionFactory();
            }
        }

        return sessionFactoryInstance;
    }

    public boolean isCurrentTransactionActive() {
        if (!isSessionOpen()) {
            return false;
        } else {
            return getSessionFactory().getCurrentSession().getTransaction().isActive();
        }
    }

    private void printTransactionError(Throwable e) {
        logger.error("Database transaction could not commit. ", e);
    }

    public void beginTransaction() {
        getSession().getTransaction().begin();
    }

    public void reinitialiseSessionFactory() {
        getSessionFactory().close();
        sessionFactoryInstance = null;
        getSessionFactory();
    }
}