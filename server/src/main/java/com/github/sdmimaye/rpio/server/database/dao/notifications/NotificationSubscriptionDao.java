package com.github.sdmimaye.rpio.server.database.dao.notifications;

import com.github.sdmimaye.rpio.server.database.dao.base.HibernateDaoBase;
import com.github.sdmimaye.rpio.server.database.models.notifications.NotificationSubscription;
import com.google.inject.Provider;
import org.hibernate.Session;

import javax.inject.Inject;

public class NotificationSubscriptionDao extends HibernateDaoBase<NotificationSubscription> {
    @Inject
    public NotificationSubscriptionDao(Provider<Session> session) {
        super(session);
    }

    @Override
    protected Class<NotificationSubscription> getModelClass() {
        return NotificationSubscription.class;
    }
}
