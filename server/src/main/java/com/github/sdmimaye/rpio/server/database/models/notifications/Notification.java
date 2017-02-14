package com.github.sdmimaye.rpio.server.database.models.notifications;

import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
public class Notification extends PersistedEntityBase {
    private String name;
    private String worker;
    private String subject;
    private String message;
    private Boolean edited = false;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.REMOVE)
    private Set<NotificationSubscription> subscriptions = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public boolean isEnabled() {
        return subscriptions.size() > 0;
    }

    public Set<NotificationSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<NotificationSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getEdited() {
        return edited;
    }

    public void setEdited(Boolean edited) {
        this.edited = edited;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "name='" + name + '\'' +
                ", worker='" + worker + '\'' +
                ", subscriptions=" + subscriptions +
                '}';
    }
}
