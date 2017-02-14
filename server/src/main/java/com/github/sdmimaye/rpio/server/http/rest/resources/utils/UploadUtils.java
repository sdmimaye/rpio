package com.github.sdmimaye.rpio.server.http.rest.resources.utils;

import javax.ws.rs.core.MultivaluedMap;

public class UploadUtils {
    public static String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

    public static long getContentLength(MultivaluedMap<String, String> header) {
        String first = header.getFirst("Content-Length");
        if(first == null)
            return 0;

        String[] contentLengths = first.split(";");
        for (String length : contentLengths) {
            try {
                return Long.parseLong(length);
            }catch (Exception ignored){}
        }
        return 0;
    }
}
