package com.github.sdmimaye.rpio.server.database.models.validation.readable;

import java.util.List;

public interface ReadableUser extends ReadableEntity {
    boolean passwordsSet();
    boolean passwordsMatching();
    String getLoginName();
    String getPassword1();
    String getPassword2();
    List<ReadableRole> getRoles();
    boolean isAdminUser();
    String getOldPassword();
    String getEmail();
}
