package com.github.sdmimaye.rpio.server.database.models.validation;

import com.github.sdmimaye.rpio.server.database.models.validation.readable.Readable;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorListBuilder {

    private List<ValidationError> errors = new ArrayList<>();

    public ValidationErrorListBuilder with(ValidationError error) {
        errors.add(error);
        return this;
    }

    public ValidationErrorListBuilder with(String error) {
        return with(new ValidationError(error));
    }

    public ValidationErrorListBuilder with(String error, Readable entity) {
        return with(new ValidationError(error, entity.toString()));
    }

    public void validate(){
        if(errors.size() > 0)
            throw new ValidationException(errors);
    }
}
