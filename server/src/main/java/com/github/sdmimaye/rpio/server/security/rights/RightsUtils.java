package com.github.sdmimaye.rpio.server.security.rights;

import com.github.sdmimaye.rpio.server.database.dao.system.UserDao;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.http.rest.models.UserData;
import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfo;
import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfoManager;
import com.google.inject.Inject;

public class RightsUtils {
    private final UserDao userDao;
    private final ActiveUserInfoManager manager;

    @Inject
    public RightsUtils(UserDao userDao, ActiveUserInfoManager manager) {
        this.userDao = userDao;
        this.manager = manager;
    }

    private User getUser() {
        if (!manager.hasInfo())
            return null;

        ActiveUserInfo info = manager.getInfo();
        User current = userDao.getById(info.getUserId());
        if (current == null)
            throw new RuntimeException("Could not get current user. ID: " + info.getUserId() + "  is unknown. Right-Management will missbehave!");

        return current;
    }

    public UserData setRights(UserData data) {
        return setRights(data, null);
    }

    public UserData setRights(UserData data, User user) {
        user = user == null ? getUser() : user;
        //Super Admin
        data.addPrivilege("superadmin-is", user != null && user.getRoles().stream().anyMatch(r -> Boolean.TRUE.equals(r.getIsAdmin())));

        return data;
    }
}
