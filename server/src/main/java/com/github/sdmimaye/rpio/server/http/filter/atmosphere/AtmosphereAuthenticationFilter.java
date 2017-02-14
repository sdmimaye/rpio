package com.github.sdmimaye.rpio.server.http.filter.atmosphere;

import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.http.rest.util.UserSessionUtil;
import com.github.sdmimaye.rpio.server.security.rights.UserRightValidator;
import com.google.inject.Inject;
import org.atmosphere.config.managed.ManagedAtmosphereHandler;
import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereInterceptorAdapter;
import org.atmosphere.cpr.AtmosphereResource;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

public class AtmosphereAuthenticationFilter extends AtmosphereInterceptorAdapter {
    private final UserSessionUtil userSessionUtil;
    private final UserRightValidator validator;

    @Inject
    public AtmosphereAuthenticationFilter(UserSessionUtil userSessionUtil, UserRightValidator validator) {
        this.userSessionUtil = userSessionUtil;
        this.validator = validator;
    }

    @Override
    public Action inspect(AtmosphereResource r) {
        ManagedAtmosphereHandler atmosphereHandler = (ManagedAtmosphereHandler) r.getAtmosphereHandler();
        if(atmosphereHandler == null)
            return Action.CANCELLED;

        Object target = atmosphereHandler.target();
        Class type = target.getClass();
        if(type.isAnnotationPresent(PermitAll.class))//abort if resource is free for all
            return super.inspect(r);

        User user = userSessionUtil.loadUser(r.session());
        if(user == null)
            return Action.CANCELLED;

        RolesAllowed allowed = (RolesAllowed) type.getAnnotation(RolesAllowed.class);
        if (allowed == null) {
            return Action.CANCELLED;
        } else {
            if(!validator.hasAllRights(user, allowed.value()))
                return Action.CANCELLED;
        }
        return super.inspect(r);
    }

    @Override
    public void postInspect(AtmosphereResource r) {

    }
}
