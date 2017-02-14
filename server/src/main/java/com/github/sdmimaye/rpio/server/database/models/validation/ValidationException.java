package com.github.sdmimaye.rpio.server.database.models.validation;

import com.github.sdmimaye.rpio.server.database.models.validation.readable.Readable;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {
    private Readable entity;
    private final List<ValidationError> errors;

    public ValidationException(ValidationError error) {
        this(Collections.singletonList(error));
    }

    public ValidationException(List<ValidationError> errors) {
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public ValidationException entity(Readable entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public String toString() {
        List<String> messages = errors.stream().map(e -> {
            if (StringUtils.isNotEmpty(e.getDetails())) {
                return e.getMessage() + " (Details: " + e.getDetails() + ")";
            }

            return e.getMessage();
        }).collect(Collectors.toList());

        return "ValidationException, Errors: " + String.join(",", messages);
    }
}
