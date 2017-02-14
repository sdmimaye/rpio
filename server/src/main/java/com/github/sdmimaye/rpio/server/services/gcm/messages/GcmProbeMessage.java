package com.github.sdmimaye.rpio.server.services.gcm.messages;

import java.util.Collections;
import java.util.List;

public class GcmProbeMessage extends GcmMessage {
    @Override
    public Object data() {
        return new ProbeContent();
    }

    private static class ProbeContent{
        private List<String> registration_ids = Collections.singletonList("ABC");

        public List<String> getRegistration_ids() {
            return registration_ids;
        }

        public void setRegistration_ids(List<String> registration_ids) {
            this.registration_ids = registration_ids;
        }
    }
}
