package com.github.sdmimaye.rpio.common.security.keystore.generator;

import org.apache.commons.lang3.ArrayUtils;

public class KeyStoreGenerationParameters {
    private char[] keyPassword;
    private char[] keyStorePassword;
    private String keyStoreType;
    private String keyStoreFilePath;

    public char[] getKeyPassword() {
        return ArrayUtils.clone(keyPassword);
    }

    public void setKeyPassword(char[] keyPassword) {
        this.keyPassword = ArrayUtils.clone(keyPassword);
    }

    public char[] getKeyStorePassword() {
        return ArrayUtils.clone(keyStorePassword);
    }

    public void setKeyStorePassword(char[] keyStorePassword) {
        this.keyStorePassword = ArrayUtils.clone(keyStorePassword);
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getKeyStoreFilePath() {
        return keyStoreFilePath;
    }

    public void setKeyStoreFilePath(String keyStoreFilePath) {
        this.keyStoreFilePath = keyStoreFilePath;
    }
}
