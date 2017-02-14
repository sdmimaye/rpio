package com.github.sdmimaye.rpio.server.http.rest.models.json.session;

import com.github.sdmimaye.rpio.server.database.models.system.Role;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadableRole;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadableUser;
import com.github.sdmimaye.rpio.server.http.rest.models.json.base.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JsonUser extends JsonObject implements ReadableUser {
    private String loginName;
    private String email;
    private String password1;
    private String password2;
    private String oldPassword;
    private Boolean isSuperAdmin;
    private List<JsonRole> roles = new ArrayList<>();
    private boolean isAdminUser;

    public JsonUser() {

    }

    public JsonUser(User user, boolean roles) {
        this.setId(user.getId());
        this.setUuid(user.getUuid());
        this.email = user.getEmail();
        this.loginName = user.getLoginName();

        if(roles)
            this.roles = JsonRole.convert(user.getRoles(), false);

        isAdminUser = user.getRoles().stream().anyMatch(Role::getIsAdmin);
        isSuperAdmin = Boolean.TRUE.equals(user.getSuperAdmin());
    }

    public static List<JsonUser> convert(Iterable<User> users, boolean roles) {
        List<JsonUser> result = new ArrayList<>();
        for (User u : users)
            result.add(new JsonUser(u, roles));

        return result;
    }

    public boolean passwordsSet() {
        return StringUtils.isNotBlank(password1) && StringUtils.isNotBlank(password2);
    }

    public boolean passwordsMatching() {
        return StringUtils.equals(password1, password2);
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public List<ReadableRole> getRoles() {
        return new ArrayList<>(roles);
    }

    public void setRoles(List<JsonRole> roles) {
        this.roles = roles;
    }

    public boolean isAdminUser() {
        return isAdminUser;
    }

    public void setAdminUser(boolean adminUser) {
        isAdminUser = adminUser;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSuperAdmin(Boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }

    public Boolean getSuperAdmin() {
        return isSuperAdmin;
    }
}
