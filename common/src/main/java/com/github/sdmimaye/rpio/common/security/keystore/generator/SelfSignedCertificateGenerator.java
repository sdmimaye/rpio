package com.github.sdmimaye.rpio.common.security.keystore.generator;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

class SelfSignedCertificateGenerator {
    private static final Logger logger = LoggerFactory.getLogger(SelfSignedCertificateGenerator.class);

    private static final Random RANDOM = new SecureRandom();
    private static final String ALIAS = "rpio";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public KeyStore generate(CertificateGenerationParameters parameters, KeyStoreGenerationParameters storeParameters) {
        try {
            Date startDate = new Date();
            Date expiryDate = parameters.getValidUntil();
            BigInteger serialNumber = BigInteger.valueOf(RANDOM.nextLong());

            KeyPair keyPair = generateKeyPair();
            SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(ASN1Sequence.getInstance(keyPair.getPublic().getEncoded()));

            X500Name dnName = new X500Name(getDn(parameters));
            X509v1CertificateBuilder certGen = new X509v1CertificateBuilder(dnName, serialNumber, startDate, expiryDate, dnName, subjectPublicKeyInfo);

            ContentSigner sigGen = new JcaContentSignerBuilder(SecurityConstants.SIG_ALGORITHM).setProvider("BC").build(keyPair.getPrivate());
            X509CertificateHolder certHolder = certGen.build(sigGen);

            logger.info("Generating self-signed certificate for {}.", getDn(parameters));
            X509Certificate x509Certificate = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

            X509Certificate[] chain = new X509Certificate[]{x509Certificate};

            KeyStore keyStore = KeyStore.getInstance(storeParameters.getKeyStoreType());
            keyStore.load(null);
            keyStore.setKeyEntry(ALIAS, keyPair.getPrivate(), storeParameters.getKeyPassword(), chain);

            return keyStore;
        } catch (Exception e) {
            throw new CertificateGenerationException("Failed to generate certificate.", e);
        }
    }

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(SecurityConstants.KEY_ALGORITHM, "BC");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        generator.initialize(SecurityConstants.KEY_SIZE, random);

        return generator.generateKeyPair();
    }

    private String getDn(CertificateGenerationParameters parameters) {
        StringBuilder builder = new StringBuilder(256);
        builder.append("CN=").append(parameters.getCommonName());
        builder.append(", O=").append(parameters.getOrganisation());
        return builder.toString();
    }
}
