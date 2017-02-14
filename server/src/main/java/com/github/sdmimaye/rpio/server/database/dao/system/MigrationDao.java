package com.github.sdmimaye.rpio.server.database.dao.system;

import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.models.enums.MigrationType;
import com.github.sdmimaye.rpio.server.database.models.system.MigrationHistory;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.hibernate.Session;

public class MigrationDao extends HibernateDaoBase<MigrationHistory> {
    @Inject
    public MigrationDao(Provider<Session> session) {
        super(session);
    }

    @Override
    protected Class<MigrationHistory> getModelClass() {
        return MigrationHistory.class;
    }

    public MigrationHistory findByIdentifierAndType(String identifier, MigrationType type){
        return (MigrationHistory) createQuery("from MigrationHistory where identifier = :identifier and type = :type")
                .setString("identifier", identifier)
                .setString("type", type.toString())
                .setMaxResults(1).uniqueResult();
    }
}
