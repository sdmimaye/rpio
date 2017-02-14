package com.github.sdmimaye.rpio.server.database.models.system;

import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;
import com.github.sdmimaye.rpio.server.database.models.enums.Right;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
public class Role extends PersistedEntityBase {
    @ManyToMany
    private Set<User> users = new HashSet<>();

    private String friendlyName;

    private Boolean isAdmin = false;

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Role allowEverything() {
        return this;
    }

    public Role prohibitEverything(){
        return this;
    }

    private Right getDefaultRight() {
        return Boolean.TRUE.equals(isAdmin) ? Right.ALL : Right.NONE;
    }

    public void createMissingRights(){
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public void addUser(User user) {
        user.addRole(this);
    }

    @Override
    public String toString() {
        return "Role{" +
                "users=" + users +
                ", friendlyName='" + friendlyName + '\'' +
                ", isAdmin=" + isAdmin +
                ", lowEverything=" + allowEverything() +
                ", ohibitEverything=" + prohibitEverything() +
                '}';
    }
}
