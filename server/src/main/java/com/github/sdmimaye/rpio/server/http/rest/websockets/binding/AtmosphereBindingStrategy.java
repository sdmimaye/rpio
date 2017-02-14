package com.github.sdmimaye.rpio.server.http.rest.websockets.binding;

import org.atmosphere.cpr.AtmosphereResource;

public interface AtmosphereBindingStrategy {
    String bind(AtmosphereResource resource, String key, Object value);
    boolean unbind(AtmosphereResource resource, String key, String value);
    <T> T read(AtmosphereResource resource, String key, Class<T> type);

    String type();
}
