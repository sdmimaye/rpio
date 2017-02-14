package com.github.sdmimaye.rpio.server.services.gcm.messages;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public abstract class GcmTargetedMessage extends GcmMessage{
    protected abstract String to();

    public String toContent() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(serialize());
    }

    private JsonGcmMessage serialize(){
        return new JsonGcmMessage(to(), data());
    }

    private static class JsonGcmMessage{
        private String to;
        private Object data;

        public JsonGcmMessage() {
        }

        public JsonGcmMessage(String to, Object data) {
            this.to = to;
            this.data = data;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}
