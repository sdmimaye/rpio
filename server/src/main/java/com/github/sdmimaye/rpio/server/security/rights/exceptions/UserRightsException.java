package com.github.sdmimaye.rpio.server.security.rights.exceptions;

public abstract class UserRightsException extends RuntimeException {
    public UserRightsException() {
    }

    public UserRightsException(String message) {
        super(message);
    }

    public UserRightsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserRightsException(Throwable cause) {
        super(cause);
    }

    public UserRightsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
