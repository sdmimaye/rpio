package com.github.sdmimaye.rpio.common.utils.threads;

public final class ThreadUtils {
    public static boolean sleep(int time){
        try{
            Thread.sleep(time);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
