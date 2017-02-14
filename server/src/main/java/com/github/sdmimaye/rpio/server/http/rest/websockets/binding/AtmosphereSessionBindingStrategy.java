package com.github.sdmimaye.rpio.server.http.rest.websockets.binding;

import org.atmosphere.cpr.AtmosphereResource;

import javax.servlet.http.HttpSession;

public class AtmosphereSessionBindingStrategy implements AtmosphereBindingStrategy {
    @Override
    public String bind(AtmosphereResource resource, String key, Object value) {
        HttpSession session = resource.session();
        if(session == null)
            return null;

        String serialize = AtmosphereBindingUtils.serialize(value);
        if(serialize == null)
            return null;

        session.setAttribute(key, serialize);
        return serialize;
    }

    @Override
    public boolean unbind(AtmosphereResource resource, String key, String value) {
        HttpSession session = resource.session(false);
        if(session == null)
            return false;

        session.removeAttribute(key);
        return true;
    }

    @Override
    public <T> T read(AtmosphereResource resource, String key, Class<T> type) {
        HttpSession session = resource.session(false);
        if(session == null)
            return null;

        Object value = session.getAttribute(key);
        if(value == null)
            return null;

        return AtmosphereBindingUtils.deserialize(type, value.toString());
    }

    @Override
    public String type() {
        return "session";
    }
}
