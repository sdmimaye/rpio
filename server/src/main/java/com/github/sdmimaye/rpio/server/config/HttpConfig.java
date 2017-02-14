package com.github.sdmimaye.rpio.server.config;

import com.github.sdmimaye.rpio.common.config.core.Config;

public class HttpConfig implements Config {
    private int httpPort;
    private int httpsPort;

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getHttpsPort() {
        return httpsPort;
    }

    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }

    @Override
    public void reset() {
        httpPort = 1180;
        httpsPort = 11443;
    }
}
