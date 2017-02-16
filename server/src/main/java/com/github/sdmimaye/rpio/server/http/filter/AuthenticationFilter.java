package com.github.sdmimaye.rpio.server.http.filter;

import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.http.rest.util.UserSessionUtil;
import com.github.sdmimaye.rpio.server.security.rights.UserRightValidator;
import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfoManager;
import com.google.inject.Inject;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final ServerResponse ACCESS_FORBIDDEN = new ServerResponse("{}", 403, new Headers<>());

    private final UserRightValidator validator;
    private final HibernateUtil hibernateUtil;
    private final UserSessionUtil userSessionUtil;
    private final ActiveUserInfoManager activeUserInfoManager;

    @Context
    private HttpServletRequest request;

    @Inject
    public AuthenticationFilter(UserRightValidator validator, HibernateUtil hibernateUtil, UserSessionUtil userSessionUtil, ActiveUserInfoManager activeUserInfoManager) {
        this.validator = validator;
        this.hibernateUtil = hibernateUtil;
        this.userSessionUtil = userSessionUtil;
        this.activeUserInfoManager = activeUserInfoManager;
    }

    private User getUser() {
        User user = activeUserInfoManager.loadUser();
        if (user != null)
            return user;

        return userSessionUtil.loadUser(request.getSession());
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
        Method method = methodInvoker.getMethod();
        if (method.isAnnotationPresent(PermitAll.class))
            return;

        User user = getUser();
        if (user == null) {
            requestContext.abortWith(ACCESS_FORBIDDEN);
        } else {
            RolesAllowed allowed = method.getAnnotation(RolesAllowed.class);
            if (allowed == null) {
                requestContext.abortWith(ACCESS_FORBIDDEN);
            } else {
                boolean hasAccess = validator.hasAllRights(user, allowed.value());
                if (!hasAccess) {
                    requestContext.abortWith(ACCESS_FORBIDDEN);
                }
            }
        }
    }
}
