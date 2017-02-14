package com.github.sdmimaye.rpio.common.security.keystore.generator;

public class CertificateGenerationException extends RuntimeException {
    public CertificateGenerationException(Throwable cause) {
        super(cause);
    }

    public CertificateGenerationException(String message) {
        super(message);
    }

    public CertificateGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
