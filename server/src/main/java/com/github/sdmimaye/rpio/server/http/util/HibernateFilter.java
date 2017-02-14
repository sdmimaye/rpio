package com.github.sdmimaye.rpio.server.http.util;

import com.github.sdmimaye.rpio.server.database.hibernate.HibernateUtil;
import com.google.inject.Inject;

import javax.servlet.*;
import java.io.IOException;

public class HibernateFilter implements Filter {
    private final HibernateUtil hibernateUtil;

    @Inject
    public HibernateFilter(HibernateUtil hibernateUtil) {
        this.hibernateUtil = hibernateUtil;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            if (!hibernateUtil.isCurrentTransactionActive()) {
                hibernateUtil.beginTransaction();
            }

            chain.doFilter(request, response);
        } finally {
            hibernateUtil.closeSession();
        }
    }

    @Override
    public void destroy() {
    }
}
