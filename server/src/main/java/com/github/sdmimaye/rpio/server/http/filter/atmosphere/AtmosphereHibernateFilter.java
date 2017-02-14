package com.github.sdmimaye.rpio.server.http.filter.atmosphere;

import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.google.inject.Inject;
import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereInterceptorAdapter;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmosphereHibernateFilter extends AtmosphereInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(AtmosphereHibernateFilter.class);
    private final HibernateUtil hibernateUtil;

    @Inject
    public AtmosphereHibernateFilter(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    @Override
    public Action inspect(AtmosphereResource r) {
        try {
            if (!hibernateUtil.isCurrentTransactionActive()) {
                hibernateUtil.beginTransaction();
            }
        } catch (Exception ex) {
            logger.warn("Could not start new Hibernate Session for Atmosphere. Did you change the Database driver? Error: " + ex.getMessage());
        }
        return super.inspect(r);
    }

    @Override
    public void postInspect(AtmosphereResource r) {
        hibernateUtil.closeSession();
    }
}
