package com.github.sdmimaye.rpio.server.http.atmosphere;

import com.google.inject.Singleton;
import org.atmosphere.guice.AtmosphereGuiceServlet;

@Singleton
public class AtmosphereRpioServlet extends AtmosphereGuiceServlet {
    public AtmosphereRpioServlet() {
        this(false, true);
    }

    public AtmosphereRpioServlet(boolean isFilter, boolean autoDetectHandlers) {
        framework = new AtmosphereRpioFramework(isFilter, autoDetectHandlers);
    }
}
