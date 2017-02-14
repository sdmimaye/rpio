package com.github.sdmimaye.rpio.server.database.models.validation.readable;

import java.util.List;

public interface ReadableRole extends ReadableEntity {
    Boolean getIsAdmin();
    List<ReadableUser> getUsers();
    String getFriendlyName();
}
