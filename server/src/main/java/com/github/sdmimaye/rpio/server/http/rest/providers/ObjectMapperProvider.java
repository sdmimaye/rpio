package com.github.sdmimaye.rpio.server.http.rest.providers;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.text.SimpleDateFormat;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private final ObjectMapper mapper;

    public ObjectMapperProvider() {
        mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
