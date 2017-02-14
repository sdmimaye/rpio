package com.github.sdmimaye.rpio.server.util.version;

import com.github.sdmimaye.rpio.common.utils.version.Version;
import com.github.sdmimaye.rpio.common.utils.version.VersionUtil;
import com.github.sdmimaye.rpio.server.database.dao.system.InstalledVersionDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.system.InstalledVersion;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class VersionUpdateUtil {
    private static final Logger logger = LoggerFactory.getLogger(VersionUpdateUtil.class);

    private final InstalledVersionDao versionDao;
    private final HibernateUtil util;

    @Inject
    public VersionUpdateUtil(InstalledVersionDao versionDao, HibernateUtil util) {
        this.versionDao = versionDao;
        this.util = util;
    }

    public void updateVersionTable() {
        try {
            util.doWork(() ->{
                Version version = Version.tryParse(VersionUtil.getVersionString(VersionUpdateUtil.class));
                int currentRevision = version.getRevision();
                InstalledVersion oldVersion = versionDao.getCurrentVersion();
                boolean oldVersionPresent = oldVersion != null;
                if (!oldVersionPresent || currentRevision != oldVersion.getRevision()) {
                    versionDao.save(InstalledVersion.byVersion(version, new Date()));
                    logger.info("New Version: " + version + " logged");
                }
            });
        }catch (Exception ex){
            logger.warn("Could not check for new Version");
        }
        finally {
            logger.info("Update check complete.");
        }
    }
}
