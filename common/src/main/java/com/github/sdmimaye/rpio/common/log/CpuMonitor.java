package com.github.sdmimaye.rpio.common.log;

import com.github.sdmimaye.rpio.common.utils.threads.ThreadUtils;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;

public class CpuMonitor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(CpuMonitor.class);

    private final TraceMethodNameBuilder traceMethodNameBuilder;

    private Set<Long> suspiciousThreadIds = new HashSet<>();
    private Map<Long, Long> lastTimeById = new HashMap<>();
    private Date lastQueryTime = new Date();

    @Inject
    CpuMonitor(TraceMethodNameBuilder traceMethodNameBuilder) {
        this.traceMethodNameBuilder = traceMethodNameBuilder;
    }

    @Override
    public void run() {
        while (ThreadUtils.sleep(20000)) {
            try {
                analyseAndLog();
            } catch (Exception e) {
                logger.warn("Error while running CpuMonitorService", e);
            }
        }
    }

    private void analyseAndLog() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] allThreadIds = threadMXBean.getAllThreadIds();
        Set<Long> nowSuspiciousThreadIds = new HashSet<>();
        Map<Long, Long> currentTimeById = new HashMap<>();
        Date thisQueryTime = new Date();
        for (long threadId : allThreadIds) {
            currentTimeById.put(threadId, threadMXBean.getThreadCpuTime(threadId));
        }

        long timeSinceLastQueryNanos = (thisQueryTime.getTime() - lastQueryTime.getTime()) * 1000000;
        for (Map.Entry<Long, Long> entry : currentTimeById.entrySet()) {
            long threadId = entry.getKey();
            long currentTime = entry.getValue();
            long usedTime;
            if (lastTimeById.containsKey(threadId)) {
                long previousTime = lastTimeById.get(threadId);
                usedTime = currentTime - previousTime;
            } else {
                usedTime = currentTime;
            }

            ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId);
            if (usedTime > 0 && threadInfo != null) {
                double usedPercentage = Math.round((double) usedTime / (double) timeSinceLastQueryNanos * 100d);
                if (usedPercentage > 50) {
                    nowSuspiciousThreadIds.add(threadId);

                    if (suspiciousThreadIds.contains(threadId)) {
                        ThreadInfo threadInfoWithTrace = threadMXBean.getThreadInfo(threadId, Integer.MAX_VALUE);

                        if (threadInfoWithTrace != null) {
                            logger.warn("High CPU load: {}% for {}: {}", usedPercentage,
                                    threadInfoWithTrace.getThreadName(),
                                    traceMethodNameBuilder.getMethodNamesByStackTrace(threadInfoWithTrace.getStackTrace(), 0, Integer.MAX_VALUE));
                        }
                    }
                }
            }
        }

        lastQueryTime = thisQueryTime;
        lastTimeById = currentTimeById;
        suspiciousThreadIds = nowSuspiciousThreadIds;
    }
}
