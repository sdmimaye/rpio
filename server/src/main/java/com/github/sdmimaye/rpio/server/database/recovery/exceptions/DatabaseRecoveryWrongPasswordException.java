package com.github.sdmimaye.rpio.server.database.recovery.exceptions;

public class DatabaseRecoveryWrongPasswordException extends DatabaseRecoveryException {
    DatabaseRecoveryWrongPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
