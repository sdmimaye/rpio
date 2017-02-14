package com.github.sdmimaye.rpio.server;

import com.github.sdmimaye.rpio.common.log.UncaughtExceptionLogger;
import com.github.sdmimaye.rpio.server.launch.Launcher;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {
    public static void main(String[] args) {
        UncaughtExceptionLogger.install();

        Injector injector = Guice.createInjector(new MainModule());

        injector.getInstance(Launcher.class).run();
    }
}
