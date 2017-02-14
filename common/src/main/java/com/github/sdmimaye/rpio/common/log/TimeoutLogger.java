package com.github.sdmimaye.rpio.common.log;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimeoutLogger implements Logger {
    private class Message{
        public Message(String message) {
            this.timestamp = LocalDateTime.now();
            this.message = message;
        }

        public LocalDateTime timestamp;
        public String message;

        @Override
        public String toString() {
            return message;
        }
    }

    private final long timeout;
    private final Logger logger;
    private final Object mutex = new Object();
    private final List<Message> messages = new ArrayList<>();

    public TimeoutLogger(long timeout, Logger logger) {
        this.timeout = timeout;
        this.logger = logger;
    }

    private boolean isLogRequired(String current) {
        synchronized (mutex) {
            cleanup();
            for (Message message : messages) {
                if(!StringUtils.equals(message.message, current))
                    continue;

                long millis = Duration.between(message.timestamp, LocalDateTime.now()).toMillis();
                return millis > timeout;
            }
            return true;
        }
    }

    private void cleanup(){
        synchronized (mutex) {
            messages.removeIf(m -> Duration.between(m.timestamp, LocalDateTime.now()).toMillis() > timeout);
        }
    }

    private void mark(String message) {
        synchronized (mutex) {
            cleanup();
            messages.removeIf(m -> StringUtils.equals(m.message, message));
            messages.add(new Message(message));
        }
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        if(!isLogRequired(msg))
            return;

        logger.trace(msg);
        mark(msg);
    }

    @Override
    public void trace(String format, Object arg) {
        if(!isLogRequired(format))
            return;

        logger.trace(format, arg);
        mark(format);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if(!isLogRequired(format))
            return;

        logger.trace(format, arg1, arg2);
        mark(format);
    }

    @Override
    public void trace(String format, Object... arguments) {
        if(!isLogRequired(format))
            return;

        logger.trace(format, arguments);
        mark(format);
    }

    @Override
    public void trace(String msg, Throwable t) {
        if(!isLogRequired(msg))
            return;

        logger.trace(msg, t);
        mark(msg);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if(!isLogRequired(msg))
            return;

        logger.trace(marker, msg);
        mark(msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if(!isLogRequired(format))
            return;

        logger.trace(marker, format, arg);
        mark(format);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if(!isLogRequired(format))
            return;

        logger.trace(marker, format, arg1, arg2);
        mark(format);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        if(!isLogRequired(format))
            return;

        logger.trace(marker, format, argArray);
        mark(format);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if(!isLogRequired(msg))
            return;

        logger.trace(marker, msg, t);
        mark(msg);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        if(!isLogRequired(msg))
            return;

        logger.debug(msg);
        mark(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        if(!isLogRequired(format))
            return;

        logger.debug(format, arg);
        mark(format);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if(!isLogRequired(format))
            return;

        logger.debug(format, arg1, arg2);
        mark(format);
    }

    @Override
    public void debug(String format, Object... arguments) {
        if(!isLogRequired(format))
            return;

        logger.debug(format, arguments);
        mark(format);
    }

    @Override
    public void debug(String msg, Throwable t) {
        if(!isLogRequired(msg))
            return;

        logger.debug(msg, t);
        mark(msg);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if(!isLogRequired(msg))
            return;

        logger.debug(marker, msg);
        mark(msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if(!isLogRequired(format))
            return;

        logger.debug(marker, format, arg);
        mark(format);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if(!isLogRequired(format))
            return;

        logger.debug(marker, format, arg1, arg2);
        mark(format);
    }

    @Override
    public void debug(Marker marker, String format, Object... argArray) {
        if(!isLogRequired(format))
            return;

        logger.debug(marker, format, argArray);
        mark(format);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if(!isLogRequired(msg))
            return;

        logger.debug(marker, msg, t);
        mark(msg);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        if(!isLogRequired(msg))
            return;

        logger.info(msg);
        mark(msg);
    }

    @Override
    public void info(String format, Object arg) {
        if(!isLogRequired(format))
            return;

        logger.info(format, arg);
        mark(format);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if(!isLogRequired(format))
            return;

        logger.info(format, arg1, arg2);
        mark(format);
    }

    @Override
    public void info(String format, Object... arguments) {
        if(!isLogRequired(format))
            return;

        logger.info(format, arguments);
        mark(format);
    }

    @Override
    public void info(String msg, Throwable t) {
        if(!isLogRequired(msg))
            return;

        logger.info(msg, t);
        mark(msg);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        if(!isLogRequired(msg))
            return;

        logger.info(marker, msg);
        mark(msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if(!isLogRequired(format))
            return;

        logger.info(marker, format, arg);
        mark(format);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if(!isLogRequired(format))
            return;

        logger.info(marker, format, arg1, arg2);
        mark(format);
    }

    @Override
    public void info(Marker marker, String format, Object... argArray) {
        if(!isLogRequired(format))
            return;

        logger.info(marker, format, argArray);
        mark(format);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if(!isLogRequired(msg))
            return;

        logger.info(marker, msg, t);
        mark(msg);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        if(!isLogRequired(msg))
            return;

        logger.warn(msg);
        mark(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        if(!isLogRequired(format))
            return;

        logger.warn(format, arg);
        mark(format);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if(!isLogRequired(format))
            return;

        logger.warn(format, arg1, arg2);
        mark(format);
    }

    @Override
    public void warn(String format, Object... arguments) {
        if(!isLogRequired(format))
            return;

        logger.warn(format, arguments);
        mark(format);
    }

    @Override
    public void warn(String msg, Throwable t) {
        if(!isLogRequired(msg))
            return;

        logger.warn(msg, t);
        mark(msg);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        if(!isLogRequired(msg))
            return;

        logger.warn(marker, msg);
        mark(msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if(!isLogRequired(format))
            return;

        logger.warn(marker, format, arg);
        mark(format);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if(!isLogRequired(format))
            return;

        logger.warn(marker, format, arg1, arg2);
        mark(format);
    }

    @Override
    public void warn(Marker marker, String format, Object... argArray) {
        if(!isLogRequired(format))
            return;

        logger.warn(marker, format, argArray);
        mark(format);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if(!isLogRequired(msg))
            return;

        logger.warn(marker, msg, t);
        mark(msg);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }





    @Override
    public void error(String msg) {
        if(!isLogRequired(msg))
            return;

        logger.error(msg);
        mark(msg);
    }

    @Override
    public void error(String format, Object arg) {
        if(!isLogRequired(format))
            return;

        logger.error(format, arg);
        mark(format);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if(!isLogRequired(format))
            return;

        logger.error(format, arg1, arg2);
        mark(format);
    }

    @Override
    public void error(String format, Object... arguments) {
        if(!isLogRequired(format))
            return;

        logger.error(format, arguments);
        mark(format);
    }

    @Override
    public void error(String msg, Throwable t) {
        if(!isLogRequired(msg))
            return;

        logger.error(msg, t);
        mark(msg);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        if(!isLogRequired(msg))
            return;

        logger.error(marker, msg);
        mark(msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if(!isLogRequired(format))
            return;

        logger.error(marker, format, arg);
        mark(format);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if(!isLogRequired(format))
            return;

        logger.error(marker, format, arg1, arg2);
        mark(format);
    }

    @Override
    public void error(Marker marker, String format, Object... argArray) {
        if(!isLogRequired(format))
            return;

        logger.error(marker, format, argArray);
        mark(format);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if(!isLogRequired(msg))
            return;

        logger.error(marker, msg, t);
        mark(msg);
    }
}
