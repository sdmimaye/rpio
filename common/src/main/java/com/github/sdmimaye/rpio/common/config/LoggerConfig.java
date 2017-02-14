package com.github.sdmimaye.rpio.common.config;

import com.github.sdmimaye.rpio.common.config.core.Config;

public class LoggerConfig implements Config {
    private String maxFileSize;
    private int maxBufferSize;

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    public void setMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    @Override
    public void reset() {
        maxFileSize = "10MB";
        maxBufferSize = 10;
    }
}
