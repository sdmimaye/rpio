package com.github.sdmimaye.rpio.server.http.rest.resources;

import com.github.sdmimaye.rpio.server.database.dao.system.RoleDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.system.Role;
import com.github.sdmimaye.rpio.server.database.models.validation.impl.RoleValidator;
import com.github.sdmimaye.rpio.server.http.rest.DeleteCommand;
import com.github.sdmimaye.rpio.server.http.rest.models.json.session.JsonRole;
import com.github.sdmimaye.rpio.server.http.rest.queries.QueryParameters;
import com.google.inject.Inject;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/role")
public class RoleResource {
    private final RoleValidator validator;
    private final RoleDao roleDao;
    private final HibernateUtil hibernateUtil;

    @Inject
    public RoleResource(RoleValidator validator, RoleDao roleDao, HibernateUtil hibernateUtil) {
        this.validator = validator;
        this.roleDao = roleDao;
        this.hibernateUtil = hibernateUtil;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("is-admin")
    public List<JsonRole> handleGetAll() {
        return JsonRole.convert(roleDao.getAll(), false);
    }

    @GET
    @Path("/query/")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("is-admin")
    public List<JsonRole> handleGetByQuery(@BeanParam QueryParameters parameters) {
        return JsonRole.convert(roleDao.getByQueryParameters(parameters), false);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("is-admin")
    public JsonRole handleGetRoleById(@PathParam("id") String roleId) {
        Role role = roleDao.getById(roleId);
        if (role == null)
            return null;

        role.createMissingRights();
        return JsonRole.convert(role, true);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("is-admin")
    public Response handleCreate(JsonRole data) {
        validator.insert(data);
        hibernateUtil.commitAndClose();
        return Response.ok(Response.Status.CREATED).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("is-admin")
    public Response handleUpdate(JsonRole updateData, @PathParam("id") long roleId) {
        validator.update(roleId, updateData);
        hibernateUtil.commitAndClose();
        return Response.ok(Response.Status.ACCEPTED).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("is-admin")
    public Response handleDelete(@PathParam("id") long roleId) {
        validator.delete(roleId);
        hibernateUtil.commitAndClose();
        return Response.ok().build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("is-admin")
    public Response handleDelete(DeleteCommand<JsonRole> deleted) {
        for (JsonRole current : deleted.getEntries()) {
            validator.delete(current.getId());
        }
        hibernateUtil.commitAndClose();
        return Response.status(Response.Status.OK).build();
    }
}
