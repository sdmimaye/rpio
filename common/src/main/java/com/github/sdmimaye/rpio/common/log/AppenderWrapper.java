package com.github.sdmimaye.rpio.common.log;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

class AppenderWrapper implements Appender {
    protected final Appender wrappedAppender;

    public AppenderWrapper(Appender wrappedAppender) {
        this.wrappedAppender = wrappedAppender;
    }

    @Override
    public void addFilter(Filter newFilter) {
        wrappedAppender.addFilter(newFilter);
    }

    @Override
    public Filter getFilter() {
        return wrappedAppender.getFilter();
    }

    @Override
    public void clearFilters() {
        wrappedAppender.clearFilters();
    }

    @Override
    public void close() {
        wrappedAppender.close();
    }

    @Override
    public void doAppend(LoggingEvent event) {
        wrappedAppender.doAppend(event);
    }

    @Override
    public String getName() {
        return wrappedAppender.getName();
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        wrappedAppender.setErrorHandler(errorHandler);
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return wrappedAppender.getErrorHandler();
    }

    @Override
    public void setLayout(Layout layout) {
        wrappedAppender.setLayout(layout);
    }

    @Override
    public Layout getLayout() {
        return wrappedAppender.getLayout();
    }

    @Override
    public void setName(String name) {
        wrappedAppender.setName(name);
    }

    @Override
    public boolean requiresLayout() {
        return wrappedAppender.requiresLayout();
    }
}
