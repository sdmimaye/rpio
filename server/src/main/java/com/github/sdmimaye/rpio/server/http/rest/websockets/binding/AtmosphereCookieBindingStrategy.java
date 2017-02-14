package com.github.sdmimaye.rpio.server.http.rest.websockets.binding;

import org.apache.commons.lang3.StringUtils;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Optional;

public class AtmosphereCookieBindingStrategy implements AtmosphereBindingStrategy {
    private static final Logger logger = LoggerFactory.getLogger(AtmosphereCookieBindingStrategy.class);

    public AtmosphereCookieBindingStrategy() {
        logger.warn(AtmosphereCookieBindingStrategy.class.getSimpleName() + " is not yet fully implemented.");
    }

    @Override
    public String bind(AtmosphereResource resource, String key, Object value) {
        AtmosphereResponse response = resource.getResponse();
        if (response == null)
            return null;

        String serialize = AtmosphereBindingUtils.serialize(value);
        if(serialize == null)
            return null;

        Cookie cookie = new Cookie(key, serialize);
        response.addCookie(cookie);

        return serialize;
    }

    private Cookie findCookie(AtmosphereResource resource, String key) {
        AtmosphereRequest request = resource.getRequest();
        if(resource == null)
            return null;

        Cookie[] cookies = request.getCookies();
        Optional<Cookie> first = Arrays.stream(cookies).filter(c -> StringUtils.equals(key, c.getName())).findFirst();
        if(!first.isPresent())
            return null;

        return first.get();
    }

    @Override
    public boolean unbind(AtmosphereResource resource, String key, String value) {
        AtmosphereResponse response = resource.getResponse();
        if(response == null)
            return false;

        Cookie cookie = findCookie(resource, key);
        if (cookie == null)
            return false;

        cookie.setValue(null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return true;
    }

    @Override
    public <T> T read(AtmosphereResource resource, String key, Class<T> type) {
        Cookie cookie = findCookie(resource, key);
        if(cookie == null)
            return null;

        String value = cookie.getValue();
        if(StringUtils.isEmpty(value))
            return null;

        return AtmosphereBindingUtils.deserialize(type, value);
    }

    @Override
    public String type() {
        return "cookie";
    }
}
