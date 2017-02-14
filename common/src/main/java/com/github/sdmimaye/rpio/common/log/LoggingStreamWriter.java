package com.github.sdmimaye.rpio.common.log;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

class LoggingStreamWriter extends PrintStream {

    public enum AutoFlushMode {
        ENABLED, DISABLED
    }

    private final Logger logger;

    private final Level level;

    private final static NullOutputStream nullOutputStream = new NullOutputStream();

    private final StringBuilder lineContentBuilder = new StringBuilder(256);

    public LoggingStreamWriter(Logger logger, Level level, AutoFlushMode autoFlushMode)
            throws IllegalArgumentException, UnsupportedEncodingException {
        super(nullOutputStream, autoFlushMode == AutoFlushMode.ENABLED, Charsets.UTF_8.name());
        this.logger = logger;
        this.level = level;

        Validate.notNull("logger", "logger must not be null");
        Validate.notNull(level, "level must not be null");
    }

    @Override
    public void println(String x) {
        logAndTerminateLine(x);
    }

    private void logAndTerminateLine(String x) {
        lineContentBuilder.append(x);
        logLine();
    }

    private void logLine() {
        String line = lineContentBuilder.toString();
        if (!line.isEmpty()) {
            logger.log(level, line);
            lineContentBuilder.setLength(0);
        }
    }

    @Override
    public void println(boolean x) {
        logAndTerminateLine(x ? "true" : "false");
    }

    @Override
    public void println() {
        logLine();
    }

    @Override
    public void println(char x) {
        logAndTerminateLine(String.valueOf(x));
    }

    @Override
    public void println(char[] x) {
        logAndTerminateLine(new String(x));
    }

    @Override
    public void println(double x) {
        logAndTerminateLine(String.valueOf(x));
    }

    @Override
    public void println(float x) {
        logAndTerminateLine(String.valueOf(x));
    }

    @Override
    public void println(int x) {
        logAndTerminateLine(String.valueOf(x));
    }

    @Override
    public void println(long x) {
        logAndTerminateLine(String.valueOf(x));
    }

    @Override
    public void println(Object x) {
        logAndTerminateLine(x == null ? "null" : x.toString());
    }

    @Override
    public void print(boolean b) {
        lineContentBuilder.append(b);
    }

    @Override
    public void print(char c) {
        lineContentBuilder.append(c);
    }

    @Override
    public void print(char[] s) {
        lineContentBuilder.append(s);
    }

    @Override
    public void print(double d) {
        lineContentBuilder.append(d);
    }

    @Override
    public void print(float f) {
        lineContentBuilder.append(f);
    }

    @Override
    public void print(int i) {
        lineContentBuilder.append(i);
    }

    @Override
    public void print(long l) {
        lineContentBuilder.append(l);
    }

    @Override
    public void print(Object obj) {
        lineContentBuilder.append(obj);
    }

    @Override
    public void print(String s) {
        lineContentBuilder.append(s);
    }

    @Override
    public PrintStream append(char c) {
        lineContentBuilder.append(c);
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq) {
        lineContentBuilder.append(this);
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end) {
        lineContentBuilder.append(csq.subSequence(start, end).toString());
        return this;
    }

    @Override
    public void write(byte[] bs) throws IOException {
        for (byte b : bs) {
            write(b & 0xFF);
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        try {
            write(Arrays.copyOfRange(buf, off, len));
        } catch (IOException e) {
        }
    }

    @Override
    public void write(int b) {
        lineContentBuilder.append((char) b);
    }
}
