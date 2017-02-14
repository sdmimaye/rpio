package com.github.sdmimaye.rpio.server.http.util;

import javax.servlet.*;
import java.io.IOException;

public class ThreadNameFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String originalThreadName = Thread.currentThread().getName();

        try {
            Thread.currentThread().setName(originalThreadName + "/" + request.getRemoteAddr());
            chain.doFilter(request, response);
        } finally {
            Thread.currentThread().setName(originalThreadName);
        }
    }

    @Override
    public void destroy() {
    }
}
