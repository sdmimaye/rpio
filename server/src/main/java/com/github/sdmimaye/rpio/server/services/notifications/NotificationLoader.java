package com.github.sdmimaye.rpio.server.services.notifications;

import java.io.InputStream;

public class NotificationLoader {
    private static final String FILE_EXTENTION = ".ntp";

    public InputStream load(String templateName) {
        try {
            String fullname = "templates/" + templateName + FILE_EXTENTION;
            return NotificationLoader.class.getResourceAsStream(fullname);
        } catch (Exception ex) {
            return null;
        }
    }
}
