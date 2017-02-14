package com.github.sdmimaye.rpio.server.database.dao.system;

import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.models.system.InstalledVersion;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.hibernate.Session;

public class InstalledVersionDao extends HibernateDaoBase<InstalledVersion> {
    @Inject
    public InstalledVersionDao(Provider<Session> session) {
        super(session);
    }

    @Override
    protected Class<InstalledVersion> getModelClass() {
        return InstalledVersion.class;
    }

    public InstalledVersion getCurrentVersion() {
        return (InstalledVersion) createQuery("from InstalledVersion order by installationDate desc").setMaxResults(1).uniqueResult();
    }
}
