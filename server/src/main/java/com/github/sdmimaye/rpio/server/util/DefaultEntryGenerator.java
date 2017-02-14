package com.github.sdmimaye.rpio.server.util;

import com.github.sdmimaye.rpio.server.database.dao.system.RoleDao;
import com.github.sdmimaye.rpio.server.database.dao.system.UserDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.system.Role;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.security.PasswordUtil;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class DefaultEntryGenerator {
    public static final String ADMIN_NAME = "admin";
    public static final String ADMIN_ROLE_NAME = "Super-Admin";

    private final HibernateUtil hibernateUtil;
    private final UserDao userDao;
    private final RoleDao roleDao;

    @Inject
    public DefaultEntryGenerator(HibernateUtil hibernateUtil, UserDao userDao, RoleDao roleDao) {
        this.hibernateUtil = hibernateUtil;
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    public void generate() {
        generate(null);
    }

    private User getAdminUser() {
        User superAdmin = userDao.getSuperAdmin();
        if (superAdmin != null)
            return superAdmin;

        //compability for older systems
        superAdmin = userDao.getByLoginName(ADMIN_NAME);
        if (superAdmin == null)
            return null;

        if (superAdmin.getRoles().stream().noneMatch(r -> Boolean.TRUE.equals(r.getIsAdmin())))
            return null;

        //found admin user without super admin flag -> set flag
        superAdmin.setSuperAdmin(true);
        return superAdmin;
    }

    public void generate(String adminPassword) {
        try {
            hibernateUtil.beginTransaction();
            Role admin = roleDao.getAdminRole();
            if (admin == null) {
                admin = roleDao.create();
                roleDao.save(admin);
            }
            admin.setFriendlyName(ADMIN_ROLE_NAME);
            admin.setIsAdmin(true);
            admin.allowEverything();
            roleDao.getAll().forEach(Role::createMissingRights);

            User root = getAdminUser();
            if (root == null) {
                adminPassword = adminPassword == null ? ADMIN_NAME : adminPassword;
                User newUser = userDao.create();
                newUser.setLoginName(ADMIN_NAME);
                newUser.setPasswordHash(PasswordUtil.calculateHash(adminPassword, newUser.getUuid()));
                newUser.setSuperAdmin(true);
                userDao.save(newUser);
            } else if (StringUtils.isNotBlank(adminPassword)) {//user is already existing
                root.setPasswordHash(PasswordUtil.calculateHash(adminPassword, root.getUuid()));
            }
            checkForAdminRole();
        } finally {
            hibernateUtil.commitAndClose();
        }
    }

    private void checkForAdminRole() {
        User admin = userDao.getByLoginName(ADMIN_NAME);
        Role role = roleDao.getByFriendlyName(ADMIN_ROLE_NAME);
        if (admin.getRoles().contains(role))
            return;

        admin.addRole(role);
    }
}
