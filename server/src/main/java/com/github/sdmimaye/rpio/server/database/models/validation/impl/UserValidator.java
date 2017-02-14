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
import com.github.sdmimaye.rpio.server.http.validation.TextValidation;
import com.github.sdmimaye.rpio.server.security.PasswordUtil;
import com.github.sdmimaye.rpio.server.util.Validator;
import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfo;
import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfoManager;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Iterator;

public class UserValidator extends ModelValidator<User, ReadableUser, UserDao> {
    private final RoleDao roleDao;
    private final ActiveUserInfoManager manager;

    @Inject
    public UserValidator(UserDao userDao, HibernateUtil util, RoleDao roleDao, ActiveUserInfoManager manager) {
        super(userDao, util);
        this.roleDao = roleDao;
        this.manager = manager;
    }

    @Override
    protected User doInsert(ReadableUser model, ValidationErrorListBuilder builder) {
        if (!model.passwordsSet())
            ValidationError.critical("passwordEmpty");

        if (StringUtils.isEmpty(model.getLoginName())) {
            ValidationError.critical("loginNameEmpty");
        }

        if (!model.passwordsMatching())
            builder.with("passwordMismatch");

        if(!TextValidation.isValidEmailAddressOrEmpty(model.getEmail()))
            builder.with("invalidEmailAddress");

        User byLoginName = dao.getByLoginName(model.getLoginName());
        if (byLoginName != null) {
            builder.with("loginTaken");
        }

        User user = dao.create();
        user.setLoginName(model.getLoginName());
        user.setEmail(model.getEmail());
        user.setPasswordHash(PasswordUtil.calculateHash(model.getPassword1(), user));
        user.setSuperAdmin(false);
        if(!addMissingRoles(user, model))
            builder.with("roleNotFound");

        dao.save(user);
        return user;
    }

    @Override
    protected User doUpdate(long id, ReadableUser model, ValidationErrorListBuilder builder) {
        User user = dao.getById(id);
        if (user == null) {
            ValidationError.critical("userNotFound");
        }
        validateUserAccess(user);

        if (StringUtils.isBlank(model.getLoginName())) {
            ValidationError.critical("loginNameEmpty");
        }

        if(!TextValidation.isValidEmailAddressOrEmpty(model.getEmail()))
            builder.with("invalidEmailAddress");

        if (!StringUtils.equalsIgnoreCase(user.getLoginName(), model.getLoginName())) {
            User byLoginName = dao.getByLoginName(model.getLoginName());
            if (byLoginName != null) {
                builder.with("loginTaken");
            }
        }
        if(!Boolean.TRUE.equals(user.getSuperAdmin()))
            user.setLoginName(model.getLoginName());

        if(!addMissingRoles(user, model))
            builder.with("roleNotFound");
        if(!removeMissingRoles(user, model))
            builder.with("lastAdminRemoval");

        if (model.passwordsSet()) {
            if (!model.passwordsMatching())
                builder.with("passwordMismatch");

            user.setPasswordHash(PasswordUtil.calculateHash(model.getPassword1(), user));
        }
        user.setEmail(model.getEmail());
        return user;
    }

    @Override
    protected User doDelete(long id, ValidationErrorListBuilder builder) {
        User user = dao.getById(id);
        if (user == null) {
            ValidationError.critical("userNotFound");
        }

        if(Boolean.TRUE.equals(user.getSuperAdmin()))
            ValidationError.critical("cannotDeleteSuperAdmin");

        removeRoles(user);
        dao.delete(user);
        return user;
    }

    public boolean canManipulateUser(User user) {
        ActiveUserInfo info = manager.getInfo();
        if(info == null)
            return false;

        User active = dao.getById(info.getUserId());
        if(active == null)
            return false;

        if(Validator.isSameEntity(info.getUserId(), user.getId()) && !active.getRoles().stream().anyMatch(r -> Boolean.TRUE.equals(r.getIsAdmin())))
            return false;

        return true;
    }

    private void removeRoles(User user) {
        Iterator<Role> roles = user.getRoles().iterator();
        while (roles.hasNext()) {
            Role next = roles.next();
            next.getUsers().remove(user);
            roles.remove();
        }
    }

    private boolean addMissingRoles(User user, ReadableUser update) {
        for (ReadableRole jrole : update.getRoles()) {
            Role role = roleDao.getById(jrole.getId());
            if(role == null)
                return false;

            if(user.getRoles().contains(role))
                continue;

            user.addRole(role);
        }

        return true;
    }

    private boolean removeMissingRoles(User user, ReadableUser updateData) {
        Iterator<Role> iterator = user.getRoles().iterator();
        while (iterator.hasNext()) {
            Role role = iterator.next();
            if(updateData.getRoles().stream().anyMatch(r -> Validator.isSameEntity(role, r)))
                continue;

            iterator.remove();
            role.getUsers().remove(user);
        }

        return true;
    }

    private void validateUserAccess(User user){
        ActiveUserInfo info = manager.getInfo();
        if(info == null)
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).build());

        User active = dao.getById(info.getUserId());
        if(active == null)
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).build());

        if(Validator.isSameEntity(info.getUserId(), user.getId()) && !active.getRoles().stream().anyMatch(r -> Boolean.TRUE.equals(r.getIsAdmin())))
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).build());
    }
}
