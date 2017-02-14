package com.github.sdmimaye.rpio.server.http.rest.modules;


import com.github.sdmimaye.rpio.server.http.filter.AuthenticationFilter;
import com.github.sdmimaye.rpio.server.http.filter.RequestLogFilter;
import com.github.sdmimaye.rpio.server.http.filter.ResponseLogFilter;
import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;

public class FilterModule extends RequestScopeModule {
    @Override
    protected void configure() {
        super.configure();
        bind(AuthenticationFilter.class);
        bind(RequestLogFilter.class);
        bind(ResponseLogFilter.class);
    }
}
