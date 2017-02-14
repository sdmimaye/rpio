package com.github.sdmimaye.rpio.common.utils.state;

public class OsUtil {
    private static final String OS_NAME = System.getProperty("os.name");
    private static final boolean IS_LINUX = OS_NAME.toLowerCase().contains("linux");
    private static final boolean IS_MAC =  OS_NAME.toLowerCase().contains("mac os x");
    private static final boolean IS_WINDOWS = OS_NAME.toLowerCase().contains("windows");

    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static boolean isLinux() {
        return IS_LINUX;
    }

    public static boolean isMac(){
        return IS_MAC;
    }

    public static boolean isUnsupportedOs() {
        return !isLinux() && !isWindows();
    }

    public static String name() {
        return OS_NAME;
    }
}
