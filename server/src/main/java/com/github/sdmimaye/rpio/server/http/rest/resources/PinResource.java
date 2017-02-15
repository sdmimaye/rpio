package com.github.sdmimaye.rpio.server.http.rest.resources;

import com.github.sdmimaye.rpio.server.database.dao.gpio.PinDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.gpio.Pin;
import com.github.sdmimaye.rpio.server.database.models.validation.ValidationError;
import com.github.sdmimaye.rpio.server.database.models.validation.impl.PinValidator;
import com.github.sdmimaye.rpio.server.http.rest.models.json.gpio.JsonPin;
import com.github.sdmimaye.rpio.server.http.rest.queries.QueryParameters;
import com.google.inject.Inject;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/pins")
public class PinResource {
    private final HibernateUtil util;
    private final PinValidator validator;
    private final PinDao dao;

    @Inject
    public PinResource(HibernateUtil util, PinValidator validator, PinDao dao) {
        this.util = util;
        this.validator = validator;
        this.dao = dao;
    }

    @GET
    @RolesAllowed("pin-read")
    @Produces(MediaType.APPLICATION_JSON)
    public List<JsonPin> handleGetAll() {
        return JsonPin.convert(dao.getAll());
    }

    @GET
    @RolesAllowed("pin-read")
    @Path("/query/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<JsonPin> handleGetByQuery(@BeanParam QueryParameters parameters) {
        return JsonPin.convert(dao.getByQueryParameters(parameters));
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("pin-read")
    public JsonPin handleGetUserById(@PathParam("id") String id) {
        Pin pin = dao.getById(id);
        if(pin == null)
            ValidationError.critical("notFound");

        return JsonPin.convert(pin);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("pin-write")
    public JsonPin handleCreate(JsonPin data) {
        Pin result = validator.insert(data);
        util.commitAndClose();

        return JsonPin.convert(result);
    }

    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("pin-write")
    public JsonPin handleUpdate(JsonPin data, @PathParam("id") long id) {
        Pin result = validator.update(id, data);
        util.commitAndClose();

        return JsonPin.convert(result);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("pin-delete")
    public JsonPin handleDelete(@PathParam("id") long id) {
        Pin pin = validator.delete(id);
        util.commitAndClose();
        return JsonPin.convert(pin);
    }
}
