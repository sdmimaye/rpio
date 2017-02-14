package com.github.sdmimaye.rpio.server.database.dao.notifications;

import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.models.notifications.NotificationChannel;
import com.google.inject.Provider;
import org.hibernate.Session;

import javax.inject.Inject;

public class NotificationChannelDao extends HibernateDaoBase<NotificationChannel> {
    @Inject
    public NotificationChannelDao(Provider<Session> session) {
        super(session);
    }

    @Override
    protected Class<NotificationChannel> getModelClass() {
        return NotificationChannel.class;
    }

    public NotificationChannel getByDescription(String description) {
        return (NotificationChannel) createQuery("from NotificationChannel where lower(description) = lower(:description)").setString("description", description).setMaxResults(1).uniqueResult();
    }
}
