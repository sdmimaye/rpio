package com.github.sdmimaye.rpio.server.database.models.validation;

import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;

public interface ModelComperator<TModel extends PersistedEntityBase, TOther> {
    boolean equals(TModel model, TOther other);
}
