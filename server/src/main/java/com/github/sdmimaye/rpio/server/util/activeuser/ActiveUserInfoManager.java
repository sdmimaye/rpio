package com.github.sdmimaye.rpio.server.util.activeuser;

import com.github.sdmimaye.rpio.server.database.dao.system.UserDao;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ActiveUserInfoManager {
    private final ThreadLocal<ActiveUserInfo> activeUserInfo = new ThreadLocal<>();

    private final UserDao userDao;

    @Inject
    public ActiveUserInfoManager(UserDao userDao) {
        this.userDao = userDao;
    }

    public ActiveUserInfo getInfo() {
        return activeUserInfo.get();
    }

    public boolean hasInfo() {
        return activeUserInfo.get() != null;
    }

    public void clearInfo() {
        activeUserInfo.remove();
    }

    public void setInfo(ActiveUserInfo value) {
        activeUserInfo.set(value);
    }

    public User loadUser() {
        return hasInfo() ? userDao.getById(getInfo().getUserId()) : null;
    }
}
