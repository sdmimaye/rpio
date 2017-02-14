package com.github.sdmimaye.rpio.server.database.recovery.exceptions;

import java.io.IOException;

public abstract class DatabaseRecoveryException extends RuntimeException {
    DatabaseRecoveryException(String message, Throwable cause) {
        super(message, cause);
    }

    public static DatabaseRecoveryException determine(IOException ioe) {
        return new DatabaseRecoveryWrongPasswordException("Wrong Password", ioe);
    }
}
