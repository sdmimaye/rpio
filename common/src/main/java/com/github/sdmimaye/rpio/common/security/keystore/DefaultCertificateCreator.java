package com.github.sdmimaye.rpio.common.security.keystore;

import com.github.sdmimaye.rpio.common.security.keystore.generator.CertificateGenerationParameters;
import com.github.sdmimaye.rpio.common.security.keystore.generator.KeyStoreGenerationParameters;
import com.google.inject.Inject;
import com.github.sdmimaye.rpio.common.security.keystore.generator.SecurityConstants;
import com.github.sdmimaye.rpio.common.security.keystore.generator.SelfSignedKeyStoreGenerator;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Date;

public class DefaultCertificateCreator {
    private static final Logger logger = LoggerFactory.getLogger(DefaultCertificateCreator.class);

    private final SelfSignedKeyStoreGenerator keyStoreGenerator;
    private final CertificateInfoProvider infoProvider;

    @Inject
    public DefaultCertificateCreator(SelfSignedKeyStoreGenerator keyStoreGenerator, CertificateInfoProvider infoProvider) {
        this.keyStoreGenerator = keyStoreGenerator;
        this.infoProvider = infoProvider;
    }

    public void createIfNecessary() {
        if (!new File(SecurityConstants.KEY_STORE_FILE).exists()) {
            logger.info("No key store existing - creating default.");
            create();
        }
    }

    public static char[] getKeyStorePassword(){
        return SecurityConstants.KEY_STORE_PASSWORD.toCharArray();
    }

    public static KeyStore getSelfSignedKeyStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, KeyManagementException {
        KeyStore store = KeyStore.getInstance(SecurityConstants.KEY_STORE_TYPE);
        store.load(new FileInputStream(SecurityConstants.KEY_STORE_FILE), getKeyStorePassword());
        return store;
    }

    private void create() {
        KeyStoreGenerationParameters storeParameters = new KeyStoreGenerationParameters();
        storeParameters.setKeyStoreFilePath(SecurityConstants.KEY_STORE_FILE);
        storeParameters.setKeyStoreType(SecurityConstants.KEY_STORE_TYPE);
        storeParameters.setKeyStorePassword(getKeyStorePassword());
        storeParameters.setKeyPassword(SecurityConstants.KEY_PASSWORD.toCharArray());

        CertificateGenerationParameters certificateParameters = new CertificateGenerationParameters();
        certificateParameters.setCommonName(infoProvider.getCommonName());
        certificateParameters.setOrganisation("sdmimaye");
        certificateParameters.setValidUntil(DateUtils.addDays(new Date(), infoProvider.getValidDays()));

        keyStoreGenerator.generateAndStore(certificateParameters, storeParameters);
    }
}
