package com.github.sdmimaye.rpio.server.services.notifications.messaging;

public class NotificationMessage {
    private String subject;
    private String message;

    public NotificationMessage() {
    }

    public NotificationMessage(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
