package com.github.sdmimaye.rpio.server.http.util;

import com.github.sdmimaye.rpio.common.security.keystore.generator.SecurityConstants;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class HttpServiceBase {
    private static final Logger logger = LoggerFactory.getLogger(HttpServiceBase.class);

    private final CountDownLatch startedLatch = new CountDownLatch(1);

    protected final void runHttpServer() {
        final String serverName = Thread.currentThread().getName();
        Server server = new Server(createStandardThreadPool());

        HttpConfiguration httpConfig = new HttpConfiguration();

        tryConnectorSetup(server, httpConfig);
        setupApp(server);
        setupServerOptions(server);

        try {
            logger.info("Starting {} server at {}", serverName, new Date());
            server.start();
            startedLatch.countDown();
            server.join();
        } catch (InterruptedException e) {
            logger.info(serverName + " interrupted.");

            try {
                server.stop();
            } catch (Exception e1) {
                logger.error("Error while stopping " + serverName, e);
            }
        } catch (Exception e) {
            logger.error("Error while executing " + serverName, e);
        }
    }

    protected abstract void setupApp(Server server);

    protected abstract void tryConnectorSetup(Server server, HttpConfiguration httpConfig);

    protected final void setupHttpConnector(Server server, int port, HttpConfiguration httpConfig) {
        ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        httpConnector.setPort(port);

        server.addConnector(httpConnector);
    }

    protected final void setupHttpsConnector(Server server, int sslPort, HttpConfiguration httpConfig) {
        RpioServerSslContextProvider provider = new RpioServerSslContextProvider();
        httpConfig.setSecureScheme("https");
        httpConfig.setSecurePort(sslPort);

        HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
        httpsConfig.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(SecurityConstants.KEY_STORE_FILE);
        sslContextFactory.setKeyStorePassword(SecurityConstants.KEY_STORE_PASSWORD);
        sslContextFactory.setKeyManagerPassword(SecurityConstants.KEY_PASSWORD);
        sslContextFactory.setSslContext(provider.getContext());
        //sslContextFactory.setWantClientAuth(true);

        ServerConnector httpsConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()), new HttpConnectionFactory(httpsConfig));
        httpsConnector.setPort(sslPort);

        server.addConnector(httpsConnector);
    }

    private QueuedThreadPool createStandardThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setName(Thread.currentThread().getName());
        threadPool.setMinThreads(1);
        threadPool.setMaxThreads(100);
        threadPool.setIdleTimeout((int) TimeUnit.MINUTES.toMillis(10));
        return threadPool;
    }

    private void setupServerOptions(Server server) {
        server.setAttribute("org.mortbay.jetty.Request.maxFormContentSize", 100000000);
    }

    protected final void setupHttpOnlySession(ServletContextHandler context) {
        HashSessionManager sessionManager = new HashSessionManager();
        sessionManager.setHttpOnly(true);
        context.setSessionHandler(new SessionHandler(sessionManager));
    }
}
