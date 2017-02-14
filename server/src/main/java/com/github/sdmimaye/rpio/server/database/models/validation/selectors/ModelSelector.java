package com.github.sdmimaye.rpio.server.database.models.validation.selectors;

import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;

public interface ModelSelector<TModel extends PersistedEntityBase, TDao extends HibernateDaoBase<TModel>> {
    TModel select(TDao dao);
}
