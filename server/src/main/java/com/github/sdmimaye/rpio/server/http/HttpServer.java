package com.github.sdmimaye.rpio.server.http;

import com.github.sdmimaye.rpio.common.config.core.Configuration;
import com.github.sdmimaye.rpio.server.config.HttpConfig;
import com.github.sdmimaye.rpio.server.http.filter.atmosphere.AtmosphereAuthenticationFilter;
import com.github.sdmimaye.rpio.server.http.filter.atmosphere.AtmosphereHibernateFilter;
import com.github.sdmimaye.rpio.server.http.rest.modules.FilterModule;
import com.github.sdmimaye.rpio.server.http.rest.modules.WebserviceModule;
import com.github.sdmimaye.rpio.server.http.rest.providers.ObjectMapperProvider;
import com.github.sdmimaye.rpio.server.http.rest.util.ActiveUserInfoFilter;
import com.github.sdmimaye.rpio.server.http.util.*;
import com.github.sdmimaye.rpio.server.http.validation.RestExceptionMapper;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.SessionSupport;
import org.atmosphere.guice.AtmosphereGuiceServlet;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

public class HttpServer extends HttpServiceBase implements Runnable {
    private final Configuration configuration;
    private final HttpServletDispatcher dispatcher;
    private final ResourceServlet resourceServlet;
    private final AtmosphereGuiceServlet atmosphereServlet;
    private final HibernateFilter hibernateFilter;
    private final Injector injector;
    private final ActiveUserInfoFilter activeUserInfoFilter;

    @Inject
    public HttpServer(Configuration configuration, HttpServletDispatcher dispatcher, ResourceServlet resourceServlet, HibernateFilter hibernateFilter,
                      Injector injector,
                      ActiveUserInfoFilter activeUserInfoFilter) {
        this.configuration = configuration;
        this.dispatcher = dispatcher;
        this.resourceServlet = resourceServlet;
        this.atmosphereServlet = new AthmosphereMykiServlet();
        this.hibernateFilter = hibernateFilter;
        this.injector = injector;
        this.activeUserInfoFilter = activeUserInfoFilter;
    }

    @Override
    public void run() {
        runHttpServer();
    }

    @Override
    protected void tryConnectorSetup(Server server, HttpConfiguration httpConfig) {
        HttpConfig config = configuration.read(HttpConfig.class);

        setupHttpConnector(server, config.getHttpPort(), httpConfig);
        setupHttpsConnector(server, config.getHttpsPort(), httpConfig);
    }

    @Override
    protected void setupApp(Server server) {
        ContextHandlerCollection contexts = new ContextHandlerCollection();

        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(contexts);
        server.setHandler(gzipHandler);

        setupRestEasyContext(contexts);
        setupStaticResourceContext(contexts);
    }

    private void setupStaticResourceContext(ContextHandlerCollection handlerCollection) {
        ServletContextHandler context = new ServletContextHandler(handlerCollection, "/", ServletContextHandler.SESSIONS);

        context.addFilter(ThreadNameFilter.class, "/*", null);
        context.addFilter(TraceRequestFilter.class, "/*", null);
        context.addFilter(CacheHeaderFilter.class, "/*", null);
        context.addServlet(new ServletHolder(resourceServlet), "/*");

        setupHttpOnlySession(context);
    }

    private void setupRestEasyContext(ContextHandlerCollection handlerCollection) {
        ServletContextHandler context = new ServletContextHandler(handlerCollection, "/api", ServletContextHandler.SESSIONS);

        context.addFilter(ThreadNameFilter.class, "/*", null);
        context.addFilter(TraceRequestFilter.class, "/*", null);
        context.addFilter(CacheHeaderFilter.class, "/*", null);
        context.addFilter(new FilterHolder(new AtmosphereGuiceAdapter(injector)), "/*", null);

        FilterHolder filterHolder = new FilterHolder(hibernateFilter);
        filterHolder.setName("HibernateFilter");
        context.addFilter(filterHolder, "/*", null);

        FilterHolder userFilterHolder = new FilterHolder(activeUserInfoFilter);
        userFilterHolder.setName("ActiveUserInfoFilter");
        context.addFilter(userFilterHolder, "/*", null);

        ServletHolder atServletHolder = new ServletHolder(atmosphereServlet);
        context.addServlet(atServletHolder, "/async/*");
        context.setInitParameter("org.atmosphere.useWebSocket", "true");
        context.setInitParameter(ApplicationConfig.PROPERTY_SESSION_SUPPORT, "true");
        context.addEventListener(new SessionSupport());
        atServletHolder.setInitParameter(ApplicationConfig.ATMOSPHERE_INTERCEPTORS, AtmosphereHibernateFilter.class.getName() + "," + AtmosphereAuthenticationFilter.class.getName());

        ServletHolder servletHolder = new ServletHolder(dispatcher);
        context.setInitParameter("resteasy.guice.modules", WebserviceModule.class.getName() + "," + FilterModule.class.getName());
        context.setInitParameter("resteasy.providers", ObjectMapperProvider.class.getName() + "," + RestExceptionMapper.class.getName());
        context.addEventListener(injector.getInstance(GuiceResteasyBootstrapServletContextListener.class));
        context.addServlet(servletHolder, "/*");
    }
}