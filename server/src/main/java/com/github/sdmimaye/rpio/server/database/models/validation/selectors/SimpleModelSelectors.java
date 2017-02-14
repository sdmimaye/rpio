package com.github.sdmimaye.rpio.server.database.models.validation.selectors;


import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;

public class SimpleModelSelectors {
    public static <TModel extends PersistedEntityBase, TDao extends HibernateDaoBase<TModel>> ModelSelector<TModel, TDao> byId(final long id) {
        return dao -> dao.getById(id);
    }

    public static <TModel extends PersistedEntityBase, TDao extends HibernateDaoBase<TModel>> ModelSelector<TModel, TDao> byUuid(final String uuid) {
        return dao -> dao.getByUuid(uuid);
    }
}
