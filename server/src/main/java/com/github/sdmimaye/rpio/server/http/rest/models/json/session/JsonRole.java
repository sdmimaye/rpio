package com.github.sdmimaye.rpio.server.http.rest.models.json.session;

import com.github.sdmimaye.rpio.server.database.models.system.Role;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadableRole;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadableUser;
import com.github.sdmimaye.rpio.server.http.rest.models.json.base.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class JsonRole extends JsonObject implements ReadableRole {
    private String friendlyName;
    private List<JsonUser> users = new ArrayList<>();
    private Boolean isAdmin;

    public JsonRole() {

    }

    public static JsonRole convert(Role role, boolean users){
        if(role == null)
            return null;

        JsonRole result = new JsonRole();
        result.setId(role.getId());
        result.setUuid(role.getUuid());
        result.setFriendlyName(role.getFriendlyName());
        result.isAdmin = role.getIsAdmin();

        if(users)
            result.users = JsonUser.convert(role.getUsers(), users);

        return result;
    }


    public static List<JsonRole> convert(Iterable<Role> collection, boolean users){
        if(collection == null)
            return null;

        List<JsonRole> result = new ArrayList<>();
        for (Role role : collection) {
            result.add(JsonRole.convert(role, users));
        }

        return result;
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public Boolean getIsAdmin() {
        return isAdmin;
    }

    @Override
    public List<ReadableUser> getUsers() {
        return new ArrayList<>(users);
    }

    public void setUsers(List<JsonUser> users) {
        this.users = users;
    }
}
