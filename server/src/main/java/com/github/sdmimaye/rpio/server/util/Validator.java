package com.github.sdmimaye.rpio.server.util;

import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadableEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class Validator {
    public static boolean isValidPort(Integer value){
        if(value == null)
            return false;

        if(value < 0 || value > 65535)
            return false;

        return true;
    }

    public static boolean isSameEntity(Long a, Long b) {
        if(a == null && b == null) return true;
        if(a == null || b == null) return false;
        return a.equals(b);
    }

    public static boolean isSameEntity(PersistedEntityBase db, ReadableEntity readable) {
        if (db == null && readable == null) return true;
        if (db == null || readable == null) return false;

        if(StringUtils.isNotEmpty(db.getUuid()) && StringUtils.isNotEmpty(readable.getUuid()))
            return Objects.equals(db.getUuid(), readable.getUuid());

        return Objects.equals(db.getId(), readable.getId());
    }
}
