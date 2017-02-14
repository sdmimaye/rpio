package com.github.sdmimaye.rpio.server.util;

public class SqlUtil {
    public static String escape(String text) {
        if(text == null)
            return null;

        return text.replaceAll("[^\\d\\w]+", "");
    }
}
