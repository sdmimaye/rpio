package com.github.sdmimaye.rpio.common.security.keystore;

public interface CertificateInfoProvider {
    String getCommonName();
    int getValidDays();
}
