package com.github.sdmimaye.rpio.server.http.rest.resources;

import com.github.sdmimaye.rpio.common.config.LoggerConfig;
import com.github.sdmimaye.rpio.common.config.core.Configuration;
import com.github.sdmimaye.rpio.common.services.ServiceManager;
import com.github.sdmimaye.rpio.server.config.DatabaseConfig;
import com.github.sdmimaye.rpio.server.config.HttpConfig;
import com.github.sdmimaye.rpio.server.config.SmtpConfig;
import com.github.sdmimaye.rpio.server.database.DatabaseTester;
import com.github.sdmimaye.rpio.server.database.dao.system.UserDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.enums.DbType;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.database.models.validation.ValidationError;
import com.github.sdmimaye.rpio.server.http.rest.annotations.StartupConfig;
import com.github.sdmimaye.rpio.server.http.rest.models.json.config.JsonBasicServerConfig;
import com.github.sdmimaye.rpio.server.http.rest.models.json.config.JsonDatabaseConfig;
import com.github.sdmimaye.rpio.server.http.rest.util.UserSessionUtil;
import com.github.sdmimaye.rpio.server.net.NetworkUtils;
import com.github.sdmimaye.rpio.server.net.smtp.FluentEmail;
import com.github.sdmimaye.rpio.server.security.rights.UserRightValidator;
import com.github.sdmimaye.rpio.server.util.DefaultEntryGenerator;
import com.github.sdmimaye.rpio.server.util.Validator;
import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfoManager;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.List;
import java.util.stream.Collectors;

@Path("/config")
public class ConfigResource {
    private static final Logger logger = LoggerFactory.getLogger(ConfigResource.class);
    private final Configuration configuration;
    private final HibernateUtil hibernateUtil;
    private final DefaultEntryGenerator defaultEntryGenerator;
    private final ServiceManager serviceManager;
    private final ActiveUserInfoManager activeUserInfoManager;
    private final UserDao userDao;
    private final UserRightValidator validator;

    private final UserSessionUtil userSessionUtil;

    @Inject
    public ConfigResource(Configuration configuration, HibernateUtil hibernateUtil, DefaultEntryGenerator defaultEntryGenerator, ServiceManager serviceManager, ActiveUserInfoManager activeUserInfoManager, UserDao userDao, UserRightValidator validator, UserSessionUtil userSessionUtil) {
        this.configuration = configuration;
        this.hibernateUtil = hibernateUtil;
        this.defaultEntryGenerator = defaultEntryGenerator;
        this.serviceManager = serviceManager;
        this.activeUserInfoManager = activeUserInfoManager;
        this.userDao = userDao;
        this.validator = validator;
        this.userSessionUtil = userSessionUtil;
    }

    @GET
    @Path("/database")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @StartupConfig
    public JsonDatabaseConfig handleGetDatabaseConfig() {
        if (!hibernateUtil.isDatabaseInitialized())//first-time setup
            return new JsonDatabaseConfig(configuration);

        User user = activeUserInfoManager.loadUser();
        if (user == null)
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        if (!validator.hasAccess(user, "is-admin"))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        return new JsonDatabaseConfig(configuration);
    }

    @GET
    @RolesAllowed("is-admin")
    @Path("/smtp")
    @Produces(MediaType.APPLICATION_JSON)
    public SmtpConfig handleGetDeadlineConfig() {
        SmtpConfig config = configuration.read(SmtpConfig.class);
        return config.noPassword();
    }

    @GET
    @Path("/basic/")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public JsonBasicServerConfig handleGetServerConfig() {
        return new JsonBasicServerConfig(configuration);
    }

    @GET
    @Path("/networkadapters/")
    @RolesAllowed("is-admin")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> handleGetNetworkAdapters() {
        return NetworkUtils.find().stream().map(Inet4Address::getHostAddress).collect(Collectors.toList());
    }

    @PUT
    @Path("/database")
    @Consumes(MediaType.APPLICATION_JSON)
    @StartupConfig
    @PermitAll
    public Response handleUpdateDatabase(JsonDatabaseConfig config) {
        if (!hibernateUtil.isDatabaseInitialized())//first-time setup
            return writeDatabaseConfiguration(config);

        User user = activeUserInfoManager.loadUser();
        if (user == null)
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        if (!validator.hasAccess(user, "is-admin"))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        return writeDatabaseConfiguration(config);
    }

    private Response writeDatabaseConfiguration(JsonDatabaseConfig config) {
        if (config.getType() == null)
            ValidationError.critical("typeNotSet");

        if (config.getType() != DbType.Local) {
            if (StringUtils.isBlank(config.getServer()))
            ValidationError.critical("hostEmpty");
            if (StringUtils.isBlank(config.getName()))
                ValidationError.critical("databaseNameEmpty");
            if (StringUtils.isBlank(config.getUsername()))
                ValidationError.critical("usernameEmpty");
        }

        boolean database = DatabaseTester.testDatabase(config.getType(), config.getServer(), config.getName(), config.getUsername(), config
                .getPassword());
        if (!database)
            ValidationError.critical("databaseError");

        setConfiguration(config);
        serviceManager.stopAllServices();
        hibernateUtil.reinitialiseSessionFactory();
        defaultEntryGenerator.generate(config.getAdminPassword());
        serviceManager.startAllServices();

        return Response.ok().build();
    }

    private void setConfiguration(JsonDatabaseConfig config) {
        DatabaseConfig db = configuration.read(DatabaseConfig.class);
        db.setType(config.getType());
        db.setDatabase(config.getName());
        db.setUser(config.getUsername());
        db.setServer(config.getServer());

        if (!JsonDatabaseConfig.PASSWORD_PLACEHOLDER.equals(config.getPassword()))
            db.setPassword(config.getPassword());

        configuration.write(db);
    }


    @PUT
    @Path("/basic")
    @RolesAllowed("is-admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleUpdateServer(JsonBasicServerConfig config) throws IOException {
        if (!Validator.isValidPort(config.getHttpPort()))
            ValidationError.critical("httpPortNotSet");
        if (!Validator.isValidPort(config.getHttpsPort()))
        ValidationError.critical("httpsPortNotSet");
        if (StringUtils.isBlank(config.getLogFileSize()))
            ValidationError.critical("logFileSizeNotSet");
        if (config.getLogFileMaxBackupSize() == null)
            ValidationError.critical("logFileMaxBackupSizeNotSet");

        HttpConfig http = configuration.read(HttpConfig.class);
        http.setHttpPort(config.getHttpPort());
        http.setHttpsPort(config.getHttpsPort());

        LoggerConfig logger = configuration.read(LoggerConfig.class);
        logger.setMaxFileSize(config.getLogFileSize());
        logger.setMaxBufferSize(config.getLogFileMaxBackupSize());

        configuration.write(http);
        configuration.write(logger);

        hibernateUtil.commitAndClose();
        return Response.ok().build();
    }

    @PUT
    @Path("/smtp/")
    @RolesAllowed("is-admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleUpdateDeadlineConfiguration(SmtpConfig settings) {
        configuration.write(settings);
        return Response.ok(Response.Status.ACCEPTED).build();
    }

    @PUT
    @Path("/smtp/test")
    @RolesAllowed("is-admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response handleTestSmtpConfiguration(@Context HttpServletRequest request, SmtpConfig configuration) {
        if(!configuration.isValidConfiguration())
            return Response.status(Response.Status.BAD_REQUEST).build();

        User user = userSessionUtil.loadUser(request.getSession());
        if (user == null)
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if (StringUtils.isEmpty(user.getEmail()))
            ValidationError.critical("missingEmailAddress");

        try {
            FluentEmail.plain()
                       .authentication(configuration.getLogin(), configuration.getPassword())
                       .from(configuration.getEmail(), configuration.getName())
                       .host(configuration.getHost())
                       .port(configuration.getPort())
                       .to(user.getEmail(), user.getLoginName())
                       .subject("Test-Message")
                       .message("This is a test message send to you by your access control system myKi")
                       .secure(configuration.getSecurity())
                       .timeout(5000)
                       .send();
            return Response.status(Response.Status.OK).build();
        } catch (Exception ex) {
            Throwable reason = ex.getCause() == null ? ex : ex.getCause();
            ValidationError.critical(reason.getLocalizedMessage());
            return null;
        }
    }
}
