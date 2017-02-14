package com.github.sdmimaye.rpio.server.database.models.validation.impl;

import com.github.sdmimaye.rpio.server.database.dao.system.RoleDao;
import com.github.sdmimaye.rpio.server.database.dao.system.UserDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.system.Role;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.database.models.validation.ModelValidator;
import com.github.sdmimaye.rpio.server.database.models.validation.ValidationError;
import com.github.sdmimaye.rpio.server.database.models.validation.ValidationErrorListBuilder;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadableRole;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadableUser;
import com.github.sdmimaye.rpio.server.util.Validator;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;

public class RoleValidator extends ModelValidator<Role, ReadableRole, RoleDao> {
    private final UserDao userDao;

    @Inject
    public RoleValidator(RoleDao roleDao, HibernateUtil util, UserDao userDao) {
        super(roleDao, util);
        this.userDao = userDao;
    }

    @Override
    protected Role doInsert(ReadableRole model, ValidationErrorListBuilder builder) {
        if (StringUtils.isBlank(model.getFriendlyName())) {
            ValidationError.critical("friendlyNameEmpty");
        }

        Role byName = dao.getByFriendlyName(model.getFriendlyName());
        if (byName != null) {
            builder.with("friendlyNameTaken");
        }

        Role role = dao.create();
        role.setFriendlyName(model.getFriendlyName());
        role.createMissingRights();
        if (!addMissingUsers(role, model))
            builder.with("userNotFound");

        dao.save(role);
        return role;
    }

    @Override
    protected Role doUpdate(long id, ReadableRole model, ValidationErrorListBuilder builder) {
        Role role = dao.getById(id);
        if (role == null)
            ValidationError.critical("roleNotFound");

        if (StringUtils.isBlank(model.getFriendlyName())) {
            ValidationError.critical("friendlyNameEmpty");
        }

        if (!StringUtils.equalsIgnoreCase(role.getFriendlyName(), model.getFriendlyName())) {
            Role byFriendlyName = dao.getByFriendlyName(model.getFriendlyName());
            if (byFriendlyName != null) {
                builder.with("friendlyNameTaken");
            }
        }

        role.setFriendlyName(model.getFriendlyName());
        role.createMissingRights();

        if (!addMissingUsers(role, model))
            builder.with("userNotFound");

        if (!removeMissingUsers(role, model))
            builder.with("lastAdminRemoval");

        return role;
    }

    @Override
    protected Role doDelete(long id, ValidationErrorListBuilder builder) {
        Role role = dao.getById(id);
        if (role == null)
            ValidationError.critical("roleNotFound");

        if (Boolean.TRUE.equals(role.getIsAdmin()))
            ValidationError.critical("adminRoleDelete");

        removeUsers(role);
        dao.delete(role);
        return role;
    }

    private boolean addMissingUsers(Role role, ReadableRole data) {
        for (ReadableUser user : data.getUsers()) {
            User fromDb = userDao.getById(user.getId());
            if (fromDb == null)
                return false;

            role.addUser(fromDb);
        }

        return true;
    }

    private boolean removeMissingUsers(Role role, ReadableRole data) {
        Iterator<User> iterator = role.getUsers().iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if(data.getUsers().stream().anyMatch(ru -> Validator.isSameEntity(user, ru)))
                continue;

            iterator.remove();
            user.getRoles().remove(role);
        }

        return true;
    }

    private void removeUsers(Role role) {
        Iterator<User> users = role.getUsers().iterator();
        while (users.hasNext()) {
            User next = users.next();
            next.getRoles().remove(role);
            users.remove();
        }
    }
}
