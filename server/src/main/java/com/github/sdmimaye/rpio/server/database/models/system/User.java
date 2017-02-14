package com.github.sdmimaye.rpio.server.database.models.system;

import com.github.sdmimaye.rpio.server.database.dao.notifications.NotificationSubscriptionDao;
import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;
import com.github.sdmimaye.rpio.server.database.models.notifications.Notification;
import com.github.sdmimaye.rpio.server.database.models.notifications.NotificationChannel;
import com.github.sdmimaye.rpio.server.database.models.notifications.NotificationSubscription;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
@Audited
public class User extends PersistedEntityBase {
    private String loginName;
    private String passwordHash;
    private String email;
    private String senderId;
    private Boolean isSuperAdmin = false;

    @ManyToMany(cascade = CascadeType.REFRESH)
    private Set<Role> roles = new HashSet<>();


    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.REMOVE)
    private Set<NotificationSubscription> subscriptions = new HashSet<>();

    public Set<NotificationSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<NotificationSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @JsonIgnore
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }

    public void setSuperAdmin(Boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getSuperAdmin() {
        return isSuperAdmin;
    }

    public void subscribe(Notification notification, NotificationChannel channel, NotificationSubscriptionDao dao) {
        Optional<NotificationSubscription> first = subscriptions.stream().filter(s -> s.getNotification().getName().equals(notification.getName())).findFirst();
        if(first != null && first.isPresent()){
            first.get().setChannel(channel);
        }else {
            NotificationSubscription subscription = new NotificationSubscription();
            subscription.setSubscriber(this);
            subscription.setChannel(channel);
            subscription.setNotification(notification);
            dao.save(subscription);
        }
    }

    public void unsubscribe(Notification notification, NotificationSubscriptionDao dao) {
        Optional<NotificationSubscription> first = subscriptions.stream().filter(s -> s.getNotification().getName().equals(notification.getName())).findFirst();
        if(first == null || !first.isPresent())
            return;//cant unsubscribe -> not subscribing

        NotificationSubscription subscription = first.get();
        notification.getSubscriptions().remove(subscription);
        subscription.setChannel(null);
        subscription.setSubscriber(null);
        dao.delete(subscription);
    }

    @Override
    public String toString() {
        return "User{" +
                "loginName='" + loginName + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", roles=" + roles.size() +
                '}';
    }
}
