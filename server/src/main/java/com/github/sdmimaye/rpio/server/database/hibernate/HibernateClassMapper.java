package com.github.sdmimaye.rpio.server.database.hibernate;

import com.github.sdmimaye.rpio.server.database.models.notifications.Notification;
import com.github.sdmimaye.rpio.server.database.models.notifications.NotificationChannel;
import com.github.sdmimaye.rpio.server.database.models.notifications.NotificationSubscription;
import com.github.sdmimaye.rpio.server.database.models.system.*;
import org.hibernate.cfg.Configuration;

class HibernateClassMapper {
    public void configureMappings(Configuration configuration) {
        //Notifications
        configuration.addAnnotatedClass(Notification.class);
        configuration.addAnnotatedClass(NotificationChannel.class);
        configuration.addAnnotatedClass(NotificationSubscription.class);

        //System
        configuration.addAnnotatedClass(InstalledVersion.class);
        configuration.addAnnotatedClass(MigrationHistory.class);
        configuration.addAnnotatedClass(RevisionEntry.class);
        configuration.addAnnotatedClass(Role.class);
        configuration.addAnnotatedClass(User.class);
    }
}