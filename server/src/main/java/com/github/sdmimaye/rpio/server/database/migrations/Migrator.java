package com.github.sdmimaye.rpio.server.database.migrations;

import com.github.sdmimaye.rpio.common.utils.ioc.InjectionUtil;
import com.github.sdmimaye.rpio.server.database.dao.system.MigrationDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.migrations.implementations.InitialisationMigration;
import com.github.sdmimaye.rpio.server.database.migrations.implementations.PinLogicMigration;
import com.github.sdmimaye.rpio.server.database.models.enums.MigrationType;
import com.github.sdmimaye.rpio.server.database.models.system.MigrationHistory;
import com.google.inject.Inject;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class Migrator {
    private static final Logger logger = Logger.getLogger(Migrator.class);
    private final List<Migration> migrations;
    private final MigrationDao dao;
    private final InjectionUtil injectionUtil;
    private final HibernateUtil util;

    @Inject
    public Migrator(MigrationDao dao, HibernateUtil util, InjectionUtil injectionUtil) {
        this.util = util;
        this.injectionUtil = injectionUtil;
        this.migrations = find();
        this.dao = dao;
    }

    private List<Migration> find() {
        return injectionUtil.getInstances(Arrays.asList(
                InitialisationMigration.class,
                PinLogicMigration.class
        ));
    }

    public void performUpdates() {
        logger.info("Starting migration");
        util.doWork(() -> {
            boolean firstTime = dao.getTotalCount() == 0;//never migrated -> first startup
            for (Migration migration : migrations) {
                MigrationHistory history = dao.findByIdentifierAndType(migration.identifier(), MigrationType.UP);
                if (history != null)//skip migration if already done once
                    continue;

                migrate(migration, !firstTime);
            }

            util.commitAndClose();
        });
        logger.info("Migration done");
    }

    private void migrate(Migration migration, boolean required) {
        if (required) {
            logger.info("Starting migration from: " + migration.identifier());
            try {
                migration.performUpdate();
            } catch (Exception ex) {
                logger.error("Error while migrating to date: " + migration.identifier() + ", Message: " + ex.getMessage());
            }
        } else {
            logger.info("Skipping migration from: " + migration.identifier());
        }
        dao.save(new MigrationHistory(migration.identifier(), MigrationType.UP));
    }
}
