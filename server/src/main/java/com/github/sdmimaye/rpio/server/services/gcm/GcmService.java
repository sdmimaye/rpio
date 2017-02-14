package com.github.sdmimaye.rpio.server.services.gcm;

import com.github.sdmimaye.rpio.common.services.RpioService;
import com.github.sdmimaye.rpio.common.utils.threads.ThreadUtils;
import com.github.sdmimaye.rpio.server.services.gcm.messages.GcmMessage;
import com.github.sdmimaye.rpio.server.services.gcm.messages.GcmProbeMessage;
import com.github.sdmimaye.rpio.server.services.gcm.messages.GcmResponse;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.ExponentialBackOff;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

@Singleton
public class GcmService implements RpioService {
    private static final String GCM_API_KEY = "AIzaSyAIeY4FtbSWrgiqXXi3h5KFXYJAhF5rWBs";
    private static final GenericUrl GCM_MESSAGE_URL = new GenericUrl("https://gcm-http.googleapis.com/gcm/send");
    private static final Logger logger = LoggerFactory.getLogger(GcmService.class);

    private final ConcurrentLinkedQueue<GcmMessage> messages = new ConcurrentLinkedQueue<>();

    private final Object mutex = new Object();
    private boolean available = false;

    @Inject
    public GcmService() {

    }

    @Override
    public void cleanup() {

    }

    public boolean isAvailable() {
        synchronized (mutex) {
            return available;
        }
    }

    @Override
    public void run() {
        logger.info("Starting Google-Cloud-Messagin Service...");
        if(!doEvaluateApiKey())
            return;

        synchronized (mutex) {
            available = true;
        }
        while (ThreadUtils.sleep(100)){
            final GcmMessage message = messages.poll();
            if(message == null)
                continue;

            try {
                doSend(message);
            }catch (Exception ex){
                logger.warn("Could not send GCM-Message", ex);
            }
        }
        synchronized (mutex) {
            available = false;
        }
    }

    private boolean doEvaluateApiKey() {
        logger.info("Start evaluation of GCM API-Key...");
        try{
            doSend(new GcmProbeMessage());
            logger.info("GCM API-Key is valid");
            return true;
        }catch (Exception ex){
            logger.warn("GCM API Key is invalid: " + ex.getMessage());
            return false;
        }
    }

    public void send(GcmMessage message) {
        messages.offer(message);
    }

    private void doSend(GcmMessage message) throws IOException {
        final String content = message.toContent();
        logger.info("Sending GCM-Message: {}, with Content: {}", message.getClass().getSimpleName(), content);

        final HttpTransport transport = new NetHttpTransport();
        final HttpRequestFactory factory = transport.createRequestFactory();
        final HttpRequest request = factory.buildPostRequest(GCM_MESSAGE_URL, new ByteArrayContent("application/json", content.getBytes("UTF-8")));

        request.setRequestMethod("POST");
        request.setLoggingEnabled(true);
        request.setCurlLoggingEnabled(true);
        request.setHeaders(new HttpHeaders()
                .setAccept("*/*")
                .setContentType("application/json")
                .setAuthorization("key=" + GCM_API_KEY)
        );

        request.setUnsuccessfulResponseHandler(new HttpBackOffUnsuccessfulResponseHandler(
                new ExponentialBackOff.Builder()
                        .setInitialIntervalMillis(500)
                        .setMaxElapsedTimeMillis(900000)
                        .setMaxIntervalMillis(6000)
                        .setMultiplier(1.5)
                        .setRandomizationFactor(0.5)
                        .build()
        ));

        final HttpResponse response = request.execute();
        final GcmResponse gcm = GcmResponse.parse(response);
        if(gcm == null){
            logger.warn("Received GCM-Response, Status: {}, Message: {}, Headers: {}. Message-Body is not recognizable!",
                    response.getStatusCode(), response.getStatusMessage(), response.getHeaders());
        }else if(gcm.isSuccess()){
            logger.info("Received GCM-Response, Status: {}, Message: {}, Headers: {}, Message-Count: {}",
                    response.getStatusCode(), response.getStatusMessage(), response.getHeaders(), gcm.getSuccess());
        }else{
            logger.error("Received GCM-Response, Status: {}, Message: {}, Headers: {}, Errors: {}",
                    response.getStatusCode(), response.getStatusMessage(), response.getHeaders(), gcm.getFailure());
        }
    }
}
