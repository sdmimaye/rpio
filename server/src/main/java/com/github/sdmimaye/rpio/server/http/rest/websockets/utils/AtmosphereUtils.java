package com.github.sdmimaye.rpio.server.http.rest.websockets.utils;

import org.apache.commons.lang3.StringUtils;
import org.atmosphere.cpr.AtmosphereResource;

import javax.servlet.http.HttpSession;

public class AtmosphereUtils {
    public static boolean isSameResource(AtmosphereResource first, AtmosphereResource second) {
        HttpSession firstSession = first.session();
        HttpSession secondSession = second.session();

        if(firstSession == null || secondSession == null)
            return false;

        return StringUtils.equals(firstSession.getId(), secondSession.getId());
    }
}
