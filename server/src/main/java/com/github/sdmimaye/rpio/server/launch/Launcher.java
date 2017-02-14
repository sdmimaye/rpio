package com.github.sdmimaye.rpio.server.launch;

import com.github.sdmimaye.rpio.common.log.CpuMonitor;
import com.github.sdmimaye.rpio.common.log.LogInitializer;
import com.github.sdmimaye.rpio.common.log.RegularServerInfoLogger;
import com.github.sdmimaye.rpio.common.security.keystore.DefaultCertificateCreator;
import com.github.sdmimaye.rpio.common.services.ServiceManager;
import com.github.sdmimaye.rpio.server.database.migrations.Migrator;
import com.github.sdmimaye.rpio.server.http.HttpServer;
import com.github.sdmimaye.rpio.server.util.DefaultEntryGenerator;
import com.github.sdmimaye.rpio.server.util.version.VersionUpdateUtil;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    private final LogInitializer logInitializer;
    private final DefaultCertificateCreator certificateCreator;
    private final HttpServer httpServer;
    private final DefaultEntryGenerator defaultEntryGenerator;
    private final RegularServerInfoLogger regularServerInfoLogger;
    private final CpuMonitor cpuMonitor;

    private final ServiceManager serviceManager;
    private final Migrator migrator;
    private final VersionUpdateUtil versionUpdateUtil;

    @Inject
    public Launcher(LogInitializer logInitializer,
                    DefaultCertificateCreator certificateCreator,
                    HttpServer httpServer,
                    DefaultEntryGenerator defaultEntryGenerator,
                    RegularServerInfoLogger regularServerInfoLogger, CpuMonitor cpuMonitor,
                    ServiceManager serviceManager,
                    Migrator migrator, VersionUpdateUtil versionUpdateUtil) {
        this.logInitializer = logInitializer;
        this.certificateCreator = certificateCreator;
        this.httpServer = httpServer;
        this.defaultEntryGenerator = defaultEntryGenerator;
        this.regularServerInfoLogger = regularServerInfoLogger;
        this.cpuMonitor = cpuMonitor;
        this.serviceManager = serviceManager;
        this.migrator = migrator;
        this.versionUpdateUtil = versionUpdateUtil;
    }

    @Override
    public void run() {
        logInitializer.initialize();
        certificateCreator.createIfNecessary();
        defaultEntryGenerator.generate();
        migrator.performUpdates();
        versionUpdateUtil.updateVersionTable();
        logger.info("Rpio starting...");

        new Thread(regularServerInfoLogger, "RegularInfo").start();
        new Thread(cpuMonitor, "CpuMonitor").start();

        new Thread(httpServer, "HTTP").start();
        this.serviceManager.startAllServices();
    }
}
