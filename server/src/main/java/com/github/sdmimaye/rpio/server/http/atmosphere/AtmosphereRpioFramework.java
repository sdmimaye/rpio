package com.github.sdmimaye.rpio.server.http.atmosphere;

import org.atmosphere.cpr.AtmosphereFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmosphereRpioFramework extends AtmosphereFramework {
    private static final Logger logger = LoggerFactory.getLogger(AtmosphereRpioFramework.class);

    public AtmosphereRpioFramework(boolean isFilter, boolean autoDetectHandlers) {
        super(isFilter, autoDetectHandlers);
    }

    @Override
    protected void analytics() {
        logger.debug("Skipping analytics()");
    }
}
