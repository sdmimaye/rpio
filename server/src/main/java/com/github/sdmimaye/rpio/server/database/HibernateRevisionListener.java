package com.github.sdmimaye.rpio.server.database;

import com.github.sdmimaye.rpio.server.database.models.system.RevisionEntry;
import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfo;
import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfoManager;
import com.google.inject.Inject;
import org.hibernate.envers.RevisionListener;

public class HibernateRevisionListener implements RevisionListener {
    private static ActiveUserInfoManager activeUserInfoManager;

    @Inject
    public static void setActiveUserInfoManager(ActiveUserInfoManager activeUserInfoManager) {
        HibernateRevisionListener.activeUserInfoManager = activeUserInfoManager;
    }

    @Override
    public void newRevision(Object revisionEntity) {
        RevisionEntry entry = (RevisionEntry) revisionEntity;
        if (activeUserInfoManager.hasInfo()) {
            ActiveUserInfo info = activeUserInfoManager.getInfo();
            entry.setUserId(info.getUserId());
            entry.setUserName(info.getUserName());
        }
    }
}
