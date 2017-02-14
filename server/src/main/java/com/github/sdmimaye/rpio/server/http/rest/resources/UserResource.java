package com.github.sdmimaye.rpio.server.http.rest.resources;

import com.github.sdmimaye.rpio.server.database.dao.system.RoleDao;
import com.github.sdmimaye.rpio.server.database.dao.system.UserDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.system.Role;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.database.models.validation.impl.UserValidator;
import com.github.sdmimaye.rpio.server.http.rest.DeleteCommand;
import com.github.sdmimaye.rpio.server.http.rest.models.json.session.JsonUser;
import com.github.sdmimaye.rpio.server.http.rest.queries.QueryParameters;
import com.google.inject.Inject;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
public class UserResource {
    public static final String ERROR_HEADER = "X-Manager-Error-Key";

    private final UserValidator validator;
    private final UserDao userDao;
    private final RoleDao roleDao;
    private final HibernateUtil hibernateUtil;

    @Inject
    public UserResource(UserValidator validator, UserDao userDao, RoleDao roleDao, HibernateUtil hibernateUtil) {
        this.validator = validator;
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.hibernateUtil = hibernateUtil;
    }

    @GET
    @RolesAllowed("is-admin")
    @Produces(MediaType.APPLICATION_JSON)
    public List<JsonUser> handleGetAll() {
        return JsonUser.convert(userDao.getAll(), false);
    }

    @GET
    @RolesAllowed("is-admin")
    @Path("/query/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<JsonUser> handleGetByQuery(@BeanParam QueryParameters parameters) {
        return JsonUser.convert(userDao.getByQueryParameters(parameters), false);
    }

    @GET
    @Path("role/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("is-admin")
    public List<JsonUser> handleGetByRoleId(@PathParam("id") String roleId) {
        Role role = roleDao.getById(roleId);
        if (role == null)
            return null;

        return JsonUser.convert(role.getUsers(), true);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public JsonUser handleGetUserById(@PathParam("id") String userId) {
        User user = userDao.getById(userId);
        if(user == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build());

        if(!validator.canManipulateUser(user))
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN).build());

        return new JsonUser(user, true);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("is-admin")
    public Response handleCreate(JsonUser newUserData) {
        validator.insert(newUserData);
        hibernateUtil.commitAndClose();
        return Response.ok(Response.Status.CREATED).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response handleUpdate(JsonUser updateData, @PathParam("id") long userId) {
        validator.update(userId, updateData);
        hibernateUtil.commitAndClose();
        return Response.ok(Response.Status.ACCEPTED).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("is-admin")
    public Response handleDelete(@PathParam("id") long userId) {
        validator.delete(userId);
        hibernateUtil.commitAndClose();
        return Response.ok(Response.Status.OK).build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("is-admin")
    public Response handleDelete(DeleteCommand<JsonUser> deleted) {
        for (JsonUser current : deleted.getEntries()) {
            validator.delete(current.getId());
        }
        hibernateUtil.commitAndClose();
        return Response.status(Response.Status.OK).build();
    }
}
