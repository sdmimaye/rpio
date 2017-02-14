package com.github.sdmimaye.rpio.server.database.dao.system;

import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.google.inject.Provider;
import org.hibernate.Session;

import javax.inject.Inject;

public class UserDao extends HibernateDaoBase<User> {
    @Inject
    public UserDao(Provider<Session> session) {
        super(session);
    }

    @Override
    protected Class<User> getModelClass() {
        return User.class;
    }

    public User getByLoginName(String loginName) {
        return (User) createQuery("from User where loginName = :loginName").setString("loginName", loginName).setMaxResults(1).uniqueResult();
    }

    public User getSuperAdmin() {
        return (User) createQuery("from User where isSuperAdmin = true").setMaxResults(1).uniqueResult();
    }
}
