package com.github.sdmimaye.rpio.server.net;

import org.apache.commons.io.Charsets;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MacAddressFinder {
    private static final String COMMAND = "arp -a ";

    public static String find(String hostnameOrAddress) {
        String mac[] = new String[5];
        String rmac[];
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(COMMAND + hostnameOrAddress);
            InputStream inputstream = proc.getInputStream();
            try(InputStreamReader inputstreamreader = new InputStreamReader(inputstream, Charsets.US_ASCII)) {
                BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
                String line;
                int i = 0;
                while ((line = bufferedreader.readLine()) != null) {
                    mac[i] = line;

                    i++;
                }
                rmac = mac[3].split("    ");
                return rmac[2].trim().toUpperCase();
            }
        } catch (Exception e) {
            return null;
        }
    }
}
