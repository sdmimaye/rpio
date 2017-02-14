package com.github.sdmimaye.rpio.common.log;

import com.github.sdmimaye.rpio.common.config.core.Configuration;
import com.github.sdmimaye.rpio.common.config.LoggerConfig;
import com.google.inject.Inject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;

public class LogInitializer {
    private static final String LOG_PATTERN = "%d [%17t] %-5p: %m%n";
    private static final String LOG_FOLDER = "log";
    private static final String LOG_TEMP_FOLDER = "temp";
    private static final String LOG_EXTENTION = ".rpl";

    private final Configuration configuration;

    @Inject
    public LogInitializer(Configuration configuration) {
        this.configuration = configuration;
    }

    public void initialize() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.removeAllAppenders();

        ensureThatFolderIsCreated();
        ensureThatTempFolderIsCreated();

        rootLogger.addAppender(new ParallelAppender(getFileAppender()));
        rootLogger.addAppender(new ParallelAppender(getConsoleAppender()));

        setUpLogLevels();

        try {
            System.setErr(new LoggingStreamWriter(rootLogger, Level.ERROR, LoggingStreamWriter.AutoFlushMode.ENABLED));
            System.setOut(new LoggingStreamWriter(rootLogger, Level.INFO, LoggingStreamWriter.AutoFlushMode.ENABLED));
        }
        catch (UnsupportedEncodingException uee){
            uee.printStackTrace();
        }
        ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);

        SLF4JBridgeHandler.install();
    }

    private void setUpLogLevels() {
        setLevel("com.schomaecker_gmbh", Level.DEBUG);

        // Jetty
        setLevel("org.eclipse.jetty", Level.INFO);

        //RestEASY
        setLevel("org.jboss.resteasy.core.SynchronousDispatcher", Level.INFO);

        //Default-Http-Client
        setLevel("org.apache.http", Level.WARN);

        // Hibernate
        setLevel("org.hibernate", Level.WARN);
        setLevel("org.hibernate.cfg.AnnotationBinder", Level.ERROR);
        setLevel("org.hibernate.SQL", Level.INFO);
        setLevel("org.hibernate.type", Level.INFO);
        setLevel("org.hibernate.tool.hbm2ddl", Level.WARN);
        setLevel("org.hibernate.pretty", Level.WARN);
        setLevel("org.hibernate.cache", Level.WARN);
        setLevel("org.hibernate.transaction", Level.INFO);
        setLevel("org.hibernate.jdbc", Level.INFO);
        setLevel("org.hibernate.cfg", Level.WARN);
        setLevel("org.hibernate.hql.ast.AST", Level.INFO);
        setLevel("org.hibernate.secure", Level.INFO);
        setLevel("org.hibernate.hql", Level.INFO);

        // c3p0 pool
        setLevel("com.mchange", Level.WARN);
        setLevel("oshi", Level.WARN);
    }

    private void setLevel(String loggerName, Level level) {
        Logger.getLogger(loggerName).setLevel(level);
    }

    private void ensureThatFolderIsCreated() {
        File folder = getLogFolder();
        if (!folder.isDirectory()) {
            if (!folder.mkdirs())
                throw new IllegalArgumentException("Log folder '" + LOG_FOLDER + "' could not be created");
        }
    }

    private void ensureThatTempFolderIsCreated() {
        File folder = getTempFolder();
        if (!folder.isDirectory()) {
            if (!folder.mkdirs())
                throw new IllegalArgumentException("Log-Temp folder '" + LOG_TEMP_FOLDER + "' could not be created");
        }
    }

    private ConsoleAppender getConsoleAppender() {
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setTarget("System.out");
        consoleAppender.setFollow(false);
        consoleAppender.setLayout(new PatternLayout(LOG_PATTERN));
        consoleAppender.activateOptions();
        return consoleAppender;
    }

    public File getLogFolder() {
        return new File(LOG_FOLDER);
    }

    public File getTempFolder() {
        return new File(getLogFolder(), LOG_TEMP_FOLDER);
    }

    public FilenameFilter getLogFileFilter() {
        return setupTimestampedFileAppender().getLogFileFilter();
    }

    private TimestampedFileAppender getFileAppender() {
        TimestampedFileAppender fileAppender = setupTimestampedFileAppender();
        fileAppender.activateOptions();
        return fileAppender;
    }

    private TimestampedFileAppender setupTimestampedFileAppender() {
        final LoggerConfig config = configuration.read(LoggerConfig.class);

        TimestampedFileAppender fileAppender = new TimestampedFileAppender();
        fileAppender.setFolder(LOG_FOLDER);
        fileAppender.setPrefix("rpio__");
        fileAppender.setDateFormat("yyyy-MM-dd__HH_mm_ss_SSS");
        fileAppender.setStartupSuffix("__start");
        fileAppender.setSuffix(LOG_EXTENTION);
        fileAppender.setMaxFileSize(config.getMaxFileSize());
        fileAppender.setMaxBackupIndex(config.getMaxBufferSize());
        fileAppender.setLayout(new PatternLayout(LOG_PATTERN));

        return fileAppender;
    }
}
