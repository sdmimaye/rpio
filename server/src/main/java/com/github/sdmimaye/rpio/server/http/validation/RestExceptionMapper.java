package com.github.sdmimaye.rpio.server.http.validation;

import com.github.sdmimaye.rpio.server.database.models.validation.ValidationException;
import com.google.inject.Singleton;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class RestExceptionMapper implements ExceptionMapper<Exception> {
    public static final int MYKI_HTTP_ERROR_CODE = 483;

    public RestExceptionMapper() {

    }

    private ValidationException unwrap(Throwable exception) {
        if(exception == null)
            return null;

        if(exception instanceof ValidationException){
            return (ValidationException) exception;
        }

        return unwrap(exception.getCause());
    }

    @Override
    public Response toResponse(Exception exception) {
        ValidationException v = unwrap(exception);
        if(v != null)
            return Response.status(MYKI_HTTP_ERROR_CODE).entity(v.getErrors()).build();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(exception.getMessage())
                       .type(MediaType.TEXT_PLAIN_TYPE).build();
    }
}
