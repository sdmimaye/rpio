package com.github.sdmimaye.rpio.server.http.rest.resources;

import com.github.sdmimaye.rpio.server.http.rest.models.json.logs.JsonServerStatus;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/status")
public class ServerStatusResource {
    @PermitAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonServerStatus handleGetAdminConfig() {
        return new JsonServerStatus();
    }
}
