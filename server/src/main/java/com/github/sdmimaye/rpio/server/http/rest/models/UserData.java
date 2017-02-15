package com.github.sdmimaye.rpio.server.http.rest.models;

import com.github.sdmimaye.rpio.common.utils.version.VersionUtil;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.http.rest.models.json.session.JsonPrivilege;

import java.util.ArrayList;
import java.util.List;

public class UserData {
    private Long id;
    private String loginName;
    private boolean loggedIn;
    private boolean isDatabaseInitialized;
    private String serverVersion;
    private List<JsonPrivilege> privileges = new ArrayList<>();

    private static UserData prepare(HibernateUtil util) {
        UserData data = new UserData();
        data.setDatabaseInitialized(util.isDatabaseInitialized());
        data.setServerVersion(VersionUtil.getVersionString(UserData.class));

        return data;
    }

    public static UserData loggedInUser(User user, HibernateUtil util) {
        UserData data = prepare(util);
        data.setLoginName(user.getLoginName());
        data.setLoggedIn(true);
        data.setId(user.getId());

        return data;
    }

    public static UserData noLoggedInUser(HibernateUtil util) {
        UserData data = prepare(util);
        data.setLoggedIn(false);
        return data;
    }

    public void addPrivilege(String name, boolean access) {
        privileges.add(new JsonPrivilege(name, access));
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public List<JsonPrivilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<JsonPrivilege> privileges) {
        this.privileges = privileges;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public boolean isDatabaseInitialized() {
        return isDatabaseInitialized;
    }

    public void setDatabaseInitialized(boolean isDatabaseInitialized) {
        this.isDatabaseInitialized = isDatabaseInitialized;
    }
}
