package com.github.sdmimaye.rpio.common.log;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

public class UncaughtExceptionLogger implements Thread.UncaughtExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionLogger.class);

    private static final UncaughtExceptionLogger instance = new UncaughtExceptionLogger();
    private final Object loggingLock = new Object();

    public static final String logName = "rpio-errors.log";

    private UncaughtExceptionLogger() {
    }

    public static void install() {
        Thread.setDefaultUncaughtExceptionHandler(instance);
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        synchronized (loggingLock) {
            tryToLogErrorToLogFileAndStderr(throwable, thread);
        }
    }

    private void tryToLogErrorToLogFileAndStderr(Throwable throwable, Thread thread) {
        try {
            logger.error("Uncaught exception in " + thread, throwable);
        } catch (Throwable t) {

        }

        File file = new File(logName);
        PrintStream logOutput = null;
        try {
            logOutput = new PrintStream(new FileOutputStream(file, true));
            printThrowable(System.err, throwable, thread);
            printThrowable(logOutput, throwable, thread);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            IOUtils.closeQuietly(logOutput);
        }
    }

    private void printThrowable(PrintStream printStream, Throwable throwable, Thread thread) {
        String firstLine = String.format("%s: Uncaught exception in %s:", new Date(), thread);
        printStream.println(firstLine);
        throwable.printStackTrace(printStream);
        printStream.println();
        printStream.println();
        printStream.flush();
    }
}
