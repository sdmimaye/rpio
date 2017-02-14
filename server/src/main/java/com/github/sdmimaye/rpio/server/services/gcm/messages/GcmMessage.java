package com.github.sdmimaye.rpio.server.services.gcm.messages;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public abstract class GcmMessage {
    public abstract Object data();
    public String toContent() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(data());
    }
}
