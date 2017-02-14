package com.github.sdmimaye.rpio.server.http.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CacheHeaderFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (StringUtils.equalsIgnoreCase(httpRequest.getHeader("X-Requested-With"), "XMLHttpRequest")) {
            httpResponse.setHeader("Expires", "-1");
            httpResponse.setHeader("Cache-Control", "no-cache, max-age=0, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
