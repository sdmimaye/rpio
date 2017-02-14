package com.github.sdmimaye.rpio.common.config.impls;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sdmimaye.rpio.common.config.core.Config;
import com.github.sdmimaye.rpio.common.config.core.Configuration;

import java.io.File;
import java.io.IOException;

public class JsonConfiguration implements Configuration {
    private final File config = new File("config");

    public JsonConfiguration() throws IOException {
        if(!config.exists() && !config.mkdir())
            throw new IOException("Could not generate configuration directory!");
    }

    private ObjectMapper getMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    private String getFileName(Class<? extends Config> type) {
        String name = type.getSimpleName();
        String cutted = name.endsWith("Config") ? name.substring(0, name.length() - 6) : name;

        return cutted + ".json";
    }

    @Override
    public <T extends Config> T read(Class<T> type) {
        try {
            File sub = new File(config, getFileName(type));
            if (!sub.exists()) {
                T instance = type.newInstance();
                instance.reset();
                write(instance);
                return instance;
            } else {
                return getMapper().readValue(sub, type);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public <T extends Config> void write(T value) {
        if(value == null) return;

        Class type = value.getClass();
        try {
            File sub = new File(config, getFileName(type));
            getMapper().writeValue(sub, value);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
