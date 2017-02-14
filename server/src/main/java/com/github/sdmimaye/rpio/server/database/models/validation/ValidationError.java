package com.github.sdmimaye.rpio.server.database.models.validation;

import com.github.sdmimaye.rpio.server.database.models.validation.readable.Readable;
import org.apache.commons.lang3.StringUtils;

public class ValidationError {
    private final String message;
    private final String details;

    public ValidationError(String message, String details) {
        this.message = message;
        this.details = details;
    }

    public ValidationError(String message) {
        this(message, null);
    }

    public static void critical(String message) {
        throw new ValidationException(new ValidationError(message));
    }
    public static void critical(String message, String details) {
        throw new ValidationException(new ValidationError(message, details));
    }
    public static void critical(String message, Readable entity) {
        throw new ValidationException(new ValidationError(message, entity.toString()));
    }
    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        if(StringUtils.isEmpty(details))
            return message;

        return message + ", " + details;
    }
}
