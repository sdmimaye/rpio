package com.github.sdmimaye.rpio.server.http.rest.util;

import com.github.sdmimaye.rpio.server.database.dao.system.UserDao;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.http.rest.models.json.session.JsonLoginCredentials;
import com.github.sdmimaye.rpio.server.security.PasswordUtil;
import com.google.inject.Inject;

import javax.servlet.http.HttpSession;

public class UserSessionUtil {
    public static final String USER_ID_ATTRIBUTE = "userId";

    private final UserDao userDao;

    @Inject
    public UserSessionUtil(UserDao userDao) {
        this.userDao = userDao;
    }

    public User loadUser(HttpSession session) {
        if (session == null) return null;

        try {
            Long userId = (Long) session.getAttribute(USER_ID_ATTRIBUTE);
            if (userId == null) {
                return null;
            } else {
                return userDao.getById(userId);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    public boolean isLoginValid(JsonLoginCredentials loginCredentials) {
        User user = userDao.getByLoginName(loginCredentials.getLoginName());
        return user != null && PasswordUtil.isValid(user, loginCredentials.getPassword());
    }

    public void loginUser(JsonLoginCredentials loginCredentials, HttpSession session) {
        User user = userDao.getByLoginName(loginCredentials.getLoginName());
        if (user == null) throw new IllegalArgumentException("Unknown user");
        session.setAttribute(USER_ID_ATTRIBUTE, user.getId());
    }

    public boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute(USER_ID_ATTRIBUTE) != null;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
