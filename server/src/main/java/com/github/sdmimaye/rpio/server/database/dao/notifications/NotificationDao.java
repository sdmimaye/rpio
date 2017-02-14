package com.github.sdmimaye.rpio.server.database.dao.notifications;

import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.models.notifications.Notification;
import com.google.inject.Provider;
import org.hibernate.Session;

import javax.inject.Inject;
import java.util.List;

public class NotificationDao extends HibernateDaoBase<Notification> {
    @Inject
    public NotificationDao(Provider<Session> session) {
        super(session);
    }

    @Override
    protected Class<Notification> getModelClass() {
        return Notification.class;
    }

    public Notification getByName(String name) {
        return (Notification) createQuery("from Notification where lower(name) = lower(:name)").setString("name", name).setMaxResults(1).uniqueResult();
    }

    public List<Notification> getAllOrdered() {
        return (List<Notification>) createQuery("from Notification order by name desc").list();
    }
}
