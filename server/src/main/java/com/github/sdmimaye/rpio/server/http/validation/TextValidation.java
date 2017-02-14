package com.github.sdmimaye.rpio.server.http.validation;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class TextValidation {
    private static final Pattern email = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    public static boolean isValidEmailAddress(String address) {
        return email.matcher(address).matches();
    }

    public static boolean isValidEmailAddressOrEmpty(String address) {
        if(StringUtils.isEmpty(address))
            return true;

        return isValidEmailAddress(address);
    }
}
