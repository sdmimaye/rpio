package com.github.sdmimaye.rpio.server.http.rest.resources;

import com.github.sdmimaye.rpio.common.utils.version.VersionUtil;
import com.github.sdmimaye.rpio.server.database.dao.system.UserDao;
import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.http.rest.models.UserData;
import com.github.sdmimaye.rpio.server.http.rest.models.json.session.JsonLoginCredentials;
import com.github.sdmimaye.rpio.server.http.rest.util.UserSessionUtil;
import com.github.sdmimaye.rpio.server.security.rights.RightsUtils;
import com.github.sdmimaye.rpio.server.services.notifications.NotificationService;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/session")
public class SessionResource {
    private static final String LOGIN_FAILED_NOTIFICATION = SessionResource.class.getSimpleName() + ".login.failed";
    private static final Logger logger = LoggerFactory.getLogger(SessionResource.class);
    private static final Object lock = new Object();

    private final RightsUtils rightsUtils;
    private final UserDao userDao;
    private final HibernateUtil hibernateUtil;
    private final UserSessionUtil userSessionUtil;
    private final NotificationService service;

    @Inject
    public SessionResource(UserSessionUtil userSessionUtil, RightsUtils rightsUtils, UserDao userDao, HibernateUtil hibernateUtil, NotificationService service) {
        this.userSessionUtil = userSessionUtil;
        this.rightsUtils = rightsUtils;
        this.userDao = userDao;
        this.hibernateUtil = hibernateUtil;
        this.service = service;

        service.register(LOGIN_FAILED_NOTIFICATION);
    }

    @PermitAll
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UserData post(JsonLoginCredentials loginCredentials, @Context HttpServletRequest request) throws InterruptedException {
        synchronized (lock) {
            if (loginCredentials == null) throw new IllegalArgumentException("login credentials must be given");

            UserData userData = new UserData();
            userData.setLoginName(loginCredentials.getLoginName());

            if (userSessionUtil.isLoginValid(loginCredentials)) {
                userSessionUtil.loginUser(loginCredentials, request.getSession());
                userData.setLoggedIn(true);

                Long userId = (Long) request.getSession().getAttribute(UserSessionUtil.USER_ID_ATTRIBUTE);
                User user = userDao.getById(userId);

                userData.setId(userId);
                userData.setDatabaseInitialized(hibernateUtil.isDatabaseInitialized());
                userData.setServerVersion(VersionUtil.getVersionString(SessionResource.class));
                userData = rightsUtils.setRights(userData, user);
            } else {
                userData.setLoggedIn(false);
                service.broadcast(LOGIN_FAILED_NOTIFICATION, loginCredentials, 0, (parameters, user) -> {
                    JsonLoginCredentials credentials = (JsonLoginCredentials) parameters;
                    return user.getLoginName().equals(credentials.getLoginName());
                });
            }
            hibernateUtil.commitAndClose();
            return userData;
        }
    }

    @PermitAll
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public UserData get(@Context HttpServletRequest request) {
        User user = userSessionUtil.loadUser(request.getSession());
        if (user != null) {
            return rightsUtils.setRights(UserData.loggedInUser(user, hibernateUtil), user);
        } else {
            return UserData.noLoggedInUser(hibernateUtil);
        }
    }

    @PermitAll
    @DELETE
    public void delete(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        logger.info("Logging out {}.", userSessionUtil.loadUser(session));
        userSessionUtil.logout(session);
    }
}
