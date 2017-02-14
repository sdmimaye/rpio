package com.github.sdmimaye.rpio.server.http;

import com.google.inject.Singleton;
import org.atmosphere.guice.AtmosphereGuiceServlet;

@Singleton
public class AthmosphereMykiServlet extends AtmosphereGuiceServlet {
    public AthmosphereMykiServlet() {
        this(false, true);
    }

    public AthmosphereMykiServlet(boolean isFilter, boolean autoDetectHandlers) {
        framework = new AtmosphereMykiFramework(isFilter, autoDetectHandlers);
    }
}
