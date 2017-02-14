package com.github.sdmimaye.rpio.server.http.util;

import com.github.sdmimaye.rpio.common.security.TrustAllManager;
import com.github.sdmimaye.rpio.common.security.keystore.DefaultCertificateCreator;

import javax.net.ssl.*;
import java.security.SecureRandom;

public class RpioServerSslContextProvider {
    private final SSLContext context;
    private final X509TrustManager trustManager = new TrustAllManager();

    public RpioServerSslContextProvider() {
        SSLContext context;
        try {
            KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            factory.init(DefaultCertificateCreator.getSelfSignedKeyStore(), DefaultCertificateCreator.getKeyStorePassword());

            KeyManager[] managers = factory.getKeyManagers();
            context = SSLContext.getInstance("SSL");
            context.init(managers, new TrustManager[]{trustManager}, new SecureRandom());
        } catch (Exception ex) {
            context = null;
        }
        this.context = context;
    }

    public SSLContext getContext() {
        return context;
    }

    public SSLSocketFactory getSslSocketFactory(){
        return context.getSocketFactory();
    }

    public X509TrustManager getTrustManager(){
        return trustManager;
    }
}
