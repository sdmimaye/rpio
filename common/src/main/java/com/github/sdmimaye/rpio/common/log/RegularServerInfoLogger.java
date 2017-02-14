package com.github.sdmimaye.rpio.common.log;

import com.github.sdmimaye.rpio.common.utils.io.SizeConverter;
import com.github.sdmimaye.rpio.common.utils.version.VersionUtil;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Date;

public class RegularServerInfoLogger implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RegularServerInfoLogger.class);

    private final MemoryMXBean memoryMxBean = ManagementFactory.getMemoryMXBean();

    @Override
    public void run() {
        String logMessage = prepareLogMessage();

        while (!Thread.currentThread().isInterrupted()) {
            logger.info(logMessage + getUsedMemoryInMB());

            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                logger.debug("Regular info logger interrupted.", e);
                return;
            }
        }
    }

    public String getUsedMemoryInMB() {
        String usedHeap = SizeConverter.convertByteToString(memoryMxBean.getHeapMemoryUsage().getUsed());
        long usedNonHeapMemory = memoryMxBean.getNonHeapMemoryUsage().getUsed();

        StringBuilder usedMemoryBuilder = new StringBuilder(128);
        usedMemoryBuilder.append(" | Used heap: ");
        usedMemoryBuilder.append(usedHeap);

        if (usedNonHeapMemory > 0) {
            usedMemoryBuilder.append(" | Used non heap: ");
            usedMemoryBuilder.append(SizeConverter.convertByteToString(usedNonHeapMemory));
        }

        return usedMemoryBuilder.toString();
    }

    private String prepareLogMessage() {
        StringBuilder logBuilder = new StringBuilder(512);

        logBuilder.append(VersionUtil.getVersionString(RegularServerInfoLogger.class));
        logBuilder.append(" | ");
        logBuilder.append(SystemUtils.OS_NAME).append(" ").append(SystemUtils.OS_VERSION).append(" ").append(SystemUtils.OS_ARCH);
        logBuilder.append(" | ");
        logBuilder.append(SystemUtils.JAVA_VM_VERSION);
        logBuilder.append(" | ");
        logBuilder.append(System.getProperty("java.vm.name")).append(" ").append(System.getProperty("java.runtime.version")).append(" ").append(System.getProperty("sun.arch.data.model")).append("bit");
        logBuilder.append(" | ");
        logBuilder.append(new Date());

        return logBuilder.toString();
    }
}
