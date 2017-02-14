package com.github.sdmimaye.rpio.server.database.hibernate;

import org.hibernate.cfg.DefaultComponentSafeNamingStrategy;

public class PrefixNamingStrategy extends DefaultComponentSafeNamingStrategy {
    private final String prefix;

    public PrefixNamingStrategy(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String classToTableName(final String className) {
        return addPrefix(super.classToTableName(className));
    }

    @Override
    public String collectionTableName(final String ownerEntity,
                                      final String ownerEntityTable, final String associatedEntity,
                                      final String associatedEntityTable, final String propertyName) {
        return addPrefix(super.collectionTableName(ownerEntity, ownerEntityTable, associatedEntity, associatedEntityTable, propertyName));
    }

    @Override
    public String logicalCollectionTableName(final String tableName,
                                             final String ownerEntityTable, final String associatedEntityTable,
                                             final String propertyName) {
        return addPrefix(super.logicalCollectionTableName(tableName, ownerEntityTable, associatedEntityTable, propertyName));
    }

    private String addPrefix(final String composedTableName) {
        return prefix + composedTableName;
    }
}
