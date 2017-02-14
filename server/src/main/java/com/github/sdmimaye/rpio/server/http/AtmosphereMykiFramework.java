package com.github.sdmimaye.rpio.server.http;

import org.atmosphere.cpr.AtmosphereFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtmosphereMykiFramework extends AtmosphereFramework {
    private static final Logger logger = LoggerFactory.getLogger(AtmosphereMykiFramework.class);

    public AtmosphereMykiFramework(boolean isFilter, boolean autoDetectHandlers) {
        super(isFilter, autoDetectHandlers);
    }

    @Override
    protected void analytics() {
        logger.debug("Skipping analytics()");
    }
}
