package com.github.sdmimaye.rpio.server.http.rest.models.json.config;

import com.github.sdmimaye.rpio.common.config.LoggerConfig;
import com.github.sdmimaye.rpio.common.config.core.Configuration;
import com.github.sdmimaye.rpio.server.config.HttpConfig;

public class JsonBasicServerConfig {
    private Integer httpPort;
    private Integer httpsPort;
    private String logFileSize;
    private Integer logFileMaxBackupSize;

    public JsonBasicServerConfig() {

    }

    public JsonBasicServerConfig(Configuration configuration) {
        LoggerConfig logger = configuration.read(LoggerConfig.class);
        HttpConfig http = configuration.read(HttpConfig.class);

        this.httpPort = http.getHttpPort();
        this.httpsPort = http.getHttpsPort();
        this.logFileSize = logger.getMaxFileSize();
        this.logFileMaxBackupSize = logger.getMaxBufferSize();
    }

    public Integer getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(Integer httpPort) {
        this.httpPort = httpPort;
    }

    public Integer getHttpsPort() {
        return httpsPort;
    }

    public void setHttpsPort(Integer httpsPort) {
        this.httpsPort = httpsPort;
    }

    public String getLogFileSize() {
        return logFileSize;
    }

    public void setLogFileSize(String logFileSize) {
        this.logFileSize = logFileSize;
    }

    public Integer getLogFileMaxBackupSize() {
        return logFileMaxBackupSize;
    }

    public void setLogFileMaxBackupSize(Integer logFileMaxBackupSize) {
        this.logFileMaxBackupSize = logFileMaxBackupSize;
    }
}
