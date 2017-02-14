package com.github.sdmimaye.rpio.server.services.notifications.messaging;

import com.github.sdmimaye.rpio.server.database.models.notifications.Notification;
import com.github.sdmimaye.rpio.server.services.notifications.NotificationLoader;
import com.github.sdmimaye.rpio.server.util.strings.PlaceholderUtil;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class NotificationMessageParser {
    private final static Charset CHARSET = Charset.forName("UTF-8");
    private final NotificationLoader loader;

    @Inject
    public NotificationMessageParser(NotificationLoader loader) {
        this.loader = loader;
    }

    public NotificationMessage parse(Notification notification, Object parameter) {
        try {
            NotificationMessage raw = raw(notification);
            return new NotificationMessage(
                    PlaceholderUtil.forItem("e", parameter).replace(raw.getSubject()),
                    PlaceholderUtil.forItem("e", parameter).replace(raw.getMessage())
            );
        } catch (Exception e) {
            throw new RuntimeException("Could not parse Template File for notification: " + notification, e);
        }
    }

    public NotificationMessage raw(Notification notification) {
        if(notification == null)
            throw new IllegalArgumentException("notification");

        if(Boolean.TRUE.equals(notification.getEdited()))
            return new NotificationMessage(notification.getSubject(), notification.getMessage());

        return getUnmodifiedTemplate(notification);
    }

    public NotificationMessage getUnmodifiedTemplate(Notification notification) {
        try (InputStream stream = loader.load(notification.getName())) {
            if(stream == null)
                return new NotificationMessage(notification.getName() + "_SUBJECT", notification.getName() + "_MESSAGE");

            try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream, CHARSET))) {
                String subject = reader.readLine();
                StringBuilder builder = new StringBuilder();
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    builder.append(line).append("\r\n");
                }
                String message = builder.toString();
                return new NotificationMessage(subject, message);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not parse Template File for notification: " + notification, e);
        }
    }

    public void save(Notification notification, NotificationMessage message) {
        if(notification == null)
            throw new IllegalArgumentException("notification");

        if(message == null)
            throw new IllegalArgumentException("message");

        boolean isDefaultTemplate = isDefaultTemplate(notification, message);
        if(isDefaultTemplate){//set not edited, delete data from database
            notification.setEdited(false);
            notification.setMessage(null);
            notification.setSubject(null);
        }else {//template differs from original template
            notification.setEdited(true);
            notification.setSubject(message.getSubject());
            notification.setMessage(message.getMessage());
        }
    }

    private boolean isDefaultTemplate(Notification notification, NotificationMessage message) {
        NotificationMessage template = getUnmodifiedTemplate(notification);
        return StringUtils.equals(message.getMessage().trim(), template.getMessage().trim()) &&
                StringUtils.equals(message.getSubject().trim(), template.getSubject().trim());
    }
}
