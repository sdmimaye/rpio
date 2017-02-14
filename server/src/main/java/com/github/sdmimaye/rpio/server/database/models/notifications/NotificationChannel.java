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
public class NotificationChannel extends PersistedEntityBase {
    private String description;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE)
    private Set<NotificationSubscription> subscriptions = new HashSet<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Set<NotificationSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<NotificationSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public String toString() {
        return "NotificationChannel{" +
                "description='" + description + '\'' +
                ", subscriptions=" + subscriptions +
                '}';
    }
}
