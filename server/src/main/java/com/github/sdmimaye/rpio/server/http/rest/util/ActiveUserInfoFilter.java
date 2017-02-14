package com.github.sdmimaye.rpio.server.http.rest.util;

import com.github.sdmimaye.rpio.server.database.models.system.User;
import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfo;
import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfoManager;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ActiveUserInfoFilter implements Filter {
    private final ActiveUserInfoManager activeUserInfoManager;
    private final UserSessionUtil sessionUtil;

    @Inject
    public ActiveUserInfoFilter(ActiveUserInfoManager activeUserInfoManager, UserSessionUtil sessionUtil) {
        this.activeUserInfoManager = activeUserInfoManager;
        this.sessionUtil = sessionUtil;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            process(request);
            chain.doFilter(request, response);
        } catch (Exception ex) {

        }
        activeUserInfoManager.clearInfo();
    }

    private void process(ServletRequest request) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        User user = sessionUtil.loadUser(httpRequest.getSession());
        if(user == null)
            return;

        activeUserInfoManager.setInfo(ActiveUserInfo.forUser(user));
        final String identifier = httpRequest.getHeader("X-Android-Identifier");
        if(StringUtils.isNotEmpty(identifier))
            user.setSenderId(identifier);
    }

    @Override
    public void destroy() {
    }
}
