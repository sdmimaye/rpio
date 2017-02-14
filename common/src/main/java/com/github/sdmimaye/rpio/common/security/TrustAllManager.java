package com.github.sdmimaye.rpio.common.security;

import com.github.sdmimaye.rpio.common.utils.io.binary.HexUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TrustAllManager implements X509TrustManager {
    private static final Logger logger = LoggerFactory.getLogger(TrustAllManager.class);

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        logger.trace("Validating Client Certificate(s) with Signature: {}",
                StringUtils.join(Arrays.stream(x509Certificates).map(c -> HexUtil.toHex(c.getSignature(), HexUtil.SpaceMode.NO_SPACES)).collect(Collectors.toList()), '|'));
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        logger.trace("Validating Server Certificate(s) with Signature: {}",
                StringUtils.join(Arrays.stream(x509Certificates).map(c -> HexUtil.toHex(c.getSignature(), HexUtil.SpaceMode.NO_SPACES)).collect(Collectors.toList()), '|'));
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
