package com.github.sdmimaye.rpio.server.http.rest.resources;

import com.github.sdmimaye.rpio.server.database.dao.gpio.GpioPinDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.gpio.GpioPin;
import com.github.sdmimaye.rpio.server.database.models.validation.ValidationError;
import com.github.sdmimaye.rpio.server.database.models.validation.impl.GpioPinValidator;
import com.github.sdmimaye.rpio.server.http.rest.models.json.gpio.JsonGpioGpioPin;
import com.github.sdmimaye.rpio.server.http.rest.queries.QueryParameters;
import com.google.inject.Inject;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/gpio")
public class GpioPinResource {
    private final HibernateUtil util;
    private final GpioPinValidator validator;
    private final GpioPinDao dao;

    @Inject
    public GpioPinResource(HibernateUtil util, GpioPinValidator validator, GpioPinDao dao) {
        this.util = util;
        this.validator = validator;
        this.dao = dao;
    }

    @GET
    @RolesAllowed("gpio-read")
    @Produces(MediaType.APPLICATION_JSON)
    public List<JsonGpioGpioPin> handleGetAll() {
        return JsonGpioGpioPin.convert(dao.getAll());
    }

    @GET
    @RolesAllowed("gpio-read")
    @Path("/query/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<JsonGpioGpioPin> handleGetByQuery(@BeanParam QueryParameters parameters) {
        return JsonGpioGpioPin.convert(dao.getByQueryParameters(parameters));
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("gpio-read")
    public JsonGpioGpioPin handleGetUserById(@PathParam("id") String id) {
        GpioPin pin = dao.getById(id);
        if(pin == null)
            ValidationError.critical("notFound");

        return JsonGpioGpioPin.convert(pin);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("gpio-write")
    public JsonGpioGpioPin handleCreate(JsonGpioGpioPin data) {
        GpioPin result = validator.insert(data);
        util.commitAndClose();

        return JsonGpioGpioPin.convert(result);
    }

    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("gpio-write")
    public JsonGpioGpioPin handleUpdate(JsonGpioGpioPin data, @PathParam("id") long id) {
        GpioPin result = validator.update(id, data);
        util.commitAndClose();

        return JsonGpioGpioPin.convert(result);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("gpio-delete")
    public JsonGpioGpioPin handleDelete(@PathParam("id") long id) {
        GpioPin pin = validator.delete(id);
        util.commitAndClose();
        return JsonGpioGpioPin.convert(pin);
    }
}
