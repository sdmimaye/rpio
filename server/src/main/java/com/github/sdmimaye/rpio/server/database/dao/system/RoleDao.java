package com.github.sdmimaye.rpio.server.database.dao.system;

import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.models.system.Role;
import com.google.inject.Provider;
import org.hibernate.Session;

import javax.inject.Inject;

public class RoleDao extends HibernateDaoBase<Role> {
    @Inject
    public RoleDao(Provider<Session> session) {
        super(session);
    }

    @Override
    protected Class<Role> getModelClass() {
        return Role.class;
    }

    public Role getAdminRole() {
        return (Role) createQuery("from Role where isAdmin = true").setMaxResults(1).uniqueResult();
    }

    public Role getByFriendlyName(String name) {
        return (Role) createQuery("from Role where friendlyName = :name").setString("name", name).setMaxResults(1).uniqueResult();
    }
}
