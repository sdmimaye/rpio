package com.github.sdmimaye.rpio.server.services.gcm.messages;

import com.google.api.client.http.HttpResponse;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class GcmResponse {
    private long multicast_id;
    private long success;
    private long failure;
    private long canonical_ids;
    private String raw;

    public GcmResponse() {

    }

    public static GcmResponse parse(final HttpResponse response) {
        try {
            String raw = IOUtils.toString(response.getContent(), "UTF-8");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            GcmResponse result = mapper.readValue(raw, GcmResponse.class);
            result.setRaw(raw);

            return result;
        } catch (IOException ioe) {
            return null;
        }
    }

    public boolean isSuccess(){
        return failure <= 0;
    }

    public long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public long getSuccess() {
        return success;
    }

    public void setSuccess(long success) {
        this.success = success;
    }

    public long getFailure() {
        return failure;
    }

    public void setFailure(long failure) {
        this.failure = failure;
    }

    public long getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(long canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    @Override
    public String toString() {
        return "GcmResponse{success: " + isSuccess() + "}";
    }
}
