package com.github.sdmimaye.rpio.server.http;

import com.google.inject.Injector;

import javax.servlet.*;
import java.io.IOException;

class AtmosphereGuiceAdapter implements Filter {
    private final Injector injector;

    AtmosphereGuiceAdapter(Injector injector) {
        this.injector = injector;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();
        context.setAttribute(Injector.class.getName(), injector);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
