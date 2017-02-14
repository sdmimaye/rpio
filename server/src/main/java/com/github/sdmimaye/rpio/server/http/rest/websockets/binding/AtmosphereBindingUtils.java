package com.github.sdmimaye.rpio.server.http.rest.websockets.binding;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class AtmosphereBindingUtils {
    public static String serialize(Object value) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return mapper.writeValueAsString(value);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T deserialize(Class<T> type, String value) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return mapper.readValue(value, type);
        } catch (IOException e) {
            return null;
        }
    }
}
