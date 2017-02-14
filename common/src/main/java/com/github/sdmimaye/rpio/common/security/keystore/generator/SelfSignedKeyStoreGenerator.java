package com.github.sdmimaye.rpio.common.security.keystore.generator;

import com.google.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class SelfSignedKeyStoreGenerator {
    private static final Logger logger = LoggerFactory.getLogger(SelfSignedKeyStoreGenerator.class);

    @Inject
    public SelfSignedKeyStoreGenerator() {
    }

    public void generateAndStore(CertificateGenerationParameters certificateParameters, KeyStoreGenerationParameters storeParameters) {
        SelfSignedCertificateGenerator generator = new SelfSignedCertificateGenerator();
        KeyStore keyStore = generator.generate(certificateParameters, storeParameters);

        tryToStore(keyStore, storeParameters);
    }

    private void tryToStore(KeyStore keyStore, KeyStoreGenerationParameters storeParameters) {
        try {
            backupExistingKeyStore(storeParameters);
            saveKeyStore(keyStore, storeParameters);
        } catch (Exception e) {
            throw new CertificateGenerationException(e);
        }
    }

    private void backupExistingKeyStore(KeyStoreGenerationParameters storeParameters) throws IOException {
        String keyStoreBackupName = storeParameters.getKeyStoreFilePath() + ".backup";
        File keyStoreFile = new File(storeParameters.getKeyStoreFilePath());
        File keyStoreBackupFile = new File(keyStoreBackupName);
        if (keyStoreFile.exists()) {
            logger.info("Backing up existing keystore '{}' as '{}'.", keyStoreFile, keyStoreBackupFile);
            FileUtils.copyFile(keyStoreFile, keyStoreBackupFile);
            FileUtils.deleteQuietly(keyStoreFile);
        }
    }

    private void saveKeyStore(KeyStore keyStore, KeyStoreGenerationParameters storeParameters)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        FileOutputStream unbufferedOutput = null;
        OutputStream keyStoreOutput = null;
        try {
            unbufferedOutput = new FileOutputStream(new File(storeParameters.getKeyStoreFilePath()));
            keyStoreOutput = new BufferedOutputStream(unbufferedOutput);
            keyStore.store(keyStoreOutput, storeParameters.getKeyStorePassword());
        } finally {
            IOUtils.closeQuietly(unbufferedOutput);
            IOUtils.closeQuietly(keyStoreOutput);
        }

        logger.info("Successfully saved new certificate to keystore.");
    }
}
