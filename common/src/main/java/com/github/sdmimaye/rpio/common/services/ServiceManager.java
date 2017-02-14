package com.github.sdmimaye.rpio.common.services;

import com.github.sdmimaye.rpio.common.utils.ioc.InjectionUtil;
import com.github.sdmimaye.rpio.common.utils.state.ApplicationState;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class ServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    private final Object lock = new Object();
    private final List<InstanceInfo> runningServices = new ArrayList<>();
    private final InjectionUtil util;
    private final RpioServiceList list;
    private final ApplicationState state;

    @Inject
    public ServiceManager(InjectionUtil util, RpioServiceList list, ApplicationState state) {
        this.util = util;
        this.list = list;
        this.state = state;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Will stop all service before application exit...");
            try {
                stopAllServices();
            } catch (Exception ex) {
                logger.warn("Error while stopping Service(s). Message: " + ex.getMessage());
            }
        }, "SM"));
    }

    public boolean isRunning() {
        synchronized (lock) {
            return !runningServices.isEmpty();
        }
    }

    private String getThreadName(RpioService service) {
        Class<? extends RpioService> type = service.getClass();
        if(type.isAnnotationPresent(Named.class))
            return type.getAnnotation(Named.class).value();

        return type.getSimpleName();
    }


    private List<Class<? extends RpioService>> filtered(List<Class<? extends RpioService>> list) {
        File file = new File("config/skipped-services.txt");
        if(!file.exists())
            return list;

        List<String> ignored = new ArrayList<>();
        logger.info("Found file to skip services...");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null){
                ignored.add(line);
            }
        } catch (Exception ex) {
            return list;
        }

        List<Class<? extends RpioService>> copy = new ArrayList<>(list);
        logger.info("Skipping Service(s): " + String.join(",", ignored));
        copy.removeIf(s -> ignored.stream().anyMatch(i -> StringUtils.equalsIgnoreCase(s.getSimpleName(), i)));
        logger.info("Remaining Service(s): " + String.join(",", copy.stream().map(s -> s.getSimpleName()).collect(Collectors.toList())));
        return copy;
    }

    public void startAllServices() {
        synchronized (lock) {
            if (isRunning())
                return;

            if(!state.isApplicationConfigurationDone()){
                logger.info("Will prevent Service Start until Application is configured...");
                return;
            }

            logger.info("Starting services...");
            List<Class<? extends RpioService>> all = list.getAllServiceTypes();
            List<Class<? extends RpioService>> filtered = filtered(all);

            for (Class<? extends RpioService> type : filtered) {
                RpioService service = util.getInstance(type);
                try {
                    Thread workerThread = new Thread(service, getThreadName(service));
                    workerThread.start();
                    runningServices.add(new InstanceInfo(service, workerThread));
                } catch (Exception e) {
                    logger.warn("Error while starting service: {}", service, e);
                }
            }
        }
    }

    public void stopAllServices() {
        synchronized (lock) {
            if (!isRunning())
                return;

            logger.info("Stopping services...");
            for (InstanceInfo instanceInfo : runningServices) {
                try {
                    try {
                        instanceInfo.getInstance().cleanup();
                    } catch (Exception e) {
                        logger.warn("Failed to cleanup {}", e);
                        logger.debug("Exception details", e);
                    }

                    instanceInfo.getWorkerThread().interrupt();
                } catch (Exception e) {
                    logger.warn("Error while stopping service: " + instanceInfo.getInstance(), e);
                }
            }

            runningServices.clear();
        }
    }

    private RpioService find(Class<? extends RpioService> type) {
        for (InstanceInfo instanceInfo : runningServices) {
            if (instanceInfo.getServiceClass().equals(type))
                return instanceInfo.getInstance();
        }

        return null;
    }

    public <T extends RestartableRpioService> void restart(Class<T> type) {
        RpioService service = find(type);
        if (service == null)
            throw new RuntimeException("Service of type " + type + " could not be found.");

        if (service instanceof RestartableRpioService) {
            logger.info("Restarting Service: " + service);
            ((RestartableRpioService) service).restart();
        } else {
            throw new RuntimeException("Service of type " + type + " is not restartable.");
        }
    }

    private class InstanceInfo {
        private final RpioService instance;
        private final Thread workerThread;

        private InstanceInfo(RpioService instance, Thread workerThread) {
            this.instance = instance;
            this.workerThread = workerThread;
        }

        public RpioService getInstance() {
            return instance;
        }

        public Thread getWorkerThread() {
            return workerThread;
        }

        public Class<? extends RpioService> getServiceClass() {
            return instance.getClass();
        }
    }
}
