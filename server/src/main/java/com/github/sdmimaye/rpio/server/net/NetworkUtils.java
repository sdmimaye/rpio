package com.github.sdmimaye.rpio.server.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtils {
    public static List<Inet4Address> find() {
        List<Inet4Address> result = new ArrayList<>();
        try {
            InetAddress[] allByName = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
            for (InetAddress address : allByName) {
                if(isFiltered(address)) continue;

                result.add((Inet4Address)address);
            }
            return result.size() > 0 ? result : fallback();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean isFiltered(InetAddress address) {
        return address.isLinkLocalAddress() || address.isLoopbackAddress() || !(address instanceof Inet4Address);
    }

    private static List<Inet4Address> fallback() throws SocketException {
        List<Inet4Address> fallback = new ArrayList<>();
        Enumeration<NetworkInterface> iter = NetworkInterface.getNetworkInterfaces();
        while (iter.hasMoreElements()) {
            NetworkInterface inter = iter.nextElement();
            Enumeration<InetAddress> addresses = inter.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if(isFiltered(addr)) continue;

                fallback.add((Inet4Address) addr);
            }
        }

        return fallback;
    }

    public static void main(String[] args) {
        List<Inet4Address> inet4Addresses = find();
        for (Inet4Address inet4Address : inet4Addresses) {
            System.out.println("Found: " + inet4Address.toString());
        }
    }
}
