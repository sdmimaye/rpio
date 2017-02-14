package com.github.sdmimaye.rpio.server.security;

import com.github.sdmimaye.rpio.server.database.models.system.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.spec.KeySpec;

public class PasswordUtil {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);

    public static boolean isValid(User user, String password) {
        return isValid(password, user.getUuid(), user.getPasswordHash());
    }

    public static boolean isValid(String password, String salt, String passwordHash) {
        return calculateHash(password, salt).equals(passwordHash);
    }

    public static String calculateHash(String password, User user) {
        return calculateHash(password, user.getUuid());
    }

    public static String calculateHash(String password, String salt) {
        try {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes("UTF-8"), 2048, 160);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = keyFactory.generateSecret(keySpec).getEncoded();
            return new BigInteger(1, hash).toString(16);
        } catch (Exception e) {
            logger.warn("Could not calculate password: {}", e.getMessage());
            logger.debug("Exception details", e);
            return "";
        }
    }
}
