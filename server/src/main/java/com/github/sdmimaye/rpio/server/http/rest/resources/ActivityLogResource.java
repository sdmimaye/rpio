package com.github.sdmimaye.rpio.server.http.rest.resources;

import com.github.sdmimaye.rpio.server.database.dao.system.RevisionEntryDao;
import com.github.sdmimaye.rpio.server.http.rest.models.json.logs.JsonActivityLogEntry;
import com.github.sdmimaye.rpio.server.http.rest.queries.QueryParameters;
import com.google.inject.Inject;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Path("/activitylog")
public class ActivityLogResource {
    public static final String ERROR_HEADER = "X-Manager-Error-Key";

    private final RevisionEntryDao revisionEntryDao;

    @Inject
    public ActivityLogResource(RevisionEntryDao revisionEntryDao) {
        this.revisionEntryDao = revisionEntryDao;
    }

    @GET
    @Path("/query")
    @RolesAllowed("activityLog-read")
    @Produces(MediaType.APPLICATION_JSON)
    public List<JsonActivityLogEntry> handleGetByQuery(@BeanParam QueryParameters parameters) {
        return revisionEntryDao.getPaged(parameters);
    }

    @GET
    @Path("{id}")
    @RolesAllowed("activityLog-read")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonActivityLogEntry handleGetUserById(@PathParam("id") String revisionId) throws IllegalAccessException, InvocationTargetException {
        return revisionEntryDao.getByRevisionId(revisionId);
    }
}
