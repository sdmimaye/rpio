package com.github.sdmimaye.rpio.server.http.rest.websockets;

import com.github.sdmimaye.rpio.server.http.rest.models.json.gpio.JsonPinState;
import com.github.sdmimaye.rpio.server.services.gpio.GpioService;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinState;
import com.github.sdmimaye.rpio.server.services.gpio.classes.GpioPinStateListener;
import com.google.inject.Inject;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@ManagedService(path = "/async/qpio")
@RolesAllowed("pin-read")
public class AsyncGpioResource implements GpioPinStateListener {
    private static final Logger logger = LoggerFactory.getLogger(AsyncGpioResource.class);

    private final Object mutex = new Object();
    private final List<AtmosphereResource> connections = new ArrayList<>();
    private final GpioService service;

    @Inject
    public AsyncGpioResource(GpioService service) {
        this.service = service;
        this.service.register(this);
    }

    @Ready
    public final void onReady(final AtmosphereResource resource) {
        synchronized (mutex) {
            connections.add(resource);
        }
        logger.info("AsyncPinResource {} connected", resource.uuid());
    }

    @Disconnect
    public final void onDisconnect(final AtmosphereResourceEvent event) {
        synchronized (mutex) {
            connections.remove(event.getResource());
        }

        if (event.isCancelled())
            logger.info("AsyncPinResource {} unexpectedly disconnected", event.getResource().uuid());
        else if (event.isClosedByClient())
            logger.info("AsyncPinResource {} closed the connection", event.getResource().uuid());
    }

    private void send(AtmosphereResource resource, Object data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String message = mapper.writeValueAsString(data);
            resource.write(message);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onPinStateChanged(int number, GpioPinState state) {
        synchronized (mutex) {
            connections.forEach(c -> send(c, new JsonPinState(number, state)));
        }
    }
}