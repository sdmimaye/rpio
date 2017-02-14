package com.github.sdmimaye.rpio.server.database.dao.base;

import com.google.inject.Provider;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

public abstract class HibernateSessionHolder {
    protected final Provider<Session> session;

    public HibernateSessionHolder(Provider<Session> session) {
        this.session = session;
    }

    protected final void flush() {
        session.get().flush();
    }

    protected final Query createQuery(String queryString) {
        return session.get().createQuery(queryString);
    }

    protected final Criteria createCriteria(Class<?> modelClass) {
        return session.get().createCriteria(modelClass);
    }

    protected final void doWork(Work work) {
        session.get().doWork(work);
    }
}
