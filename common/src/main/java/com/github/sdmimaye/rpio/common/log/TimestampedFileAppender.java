package com.github.sdmimaye.rpio.common.log;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;
import java.util.Vector;

class TimestampedFileAppender extends FileAppender {
    private long maxFileSize = 16777216;
    private int maxBackupIndex = 128;
    private String prefix = "myki__";
    private String suffix = ".log";
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd__HH_mm_ss_SSS");
    private String folder = "";
    private String startupSuffix = "";

    public void setFolder(String folder) {
        int length = folder.length();
        if (length > 1 && folder.endsWith(File.separator)) {
            this.folder = StringUtils.left(folder, length - 1);
        } else {
            this.folder = folder;
        }
    }

    private String generateFileName() {
        return folder + File.separator + prefix + dateFormat.format(new Date()) + suffix;
    }

    private String generateStartupFileName() {
        return folder + File.separator + prefix + dateFormat.format(new Date()) + startupSuffix + suffix;
    }

    @Override
    public void activateOptions() {
        try {
            setFile(generateStartupFileName(), true, bufferedIO, bufferSize);
        } catch (IOException e) {
            if (e instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            LogLog.error("setFile(" + fileName + ", true) call failed.", e);
        }
    }

    @Override
    protected void subAppend(LoggingEvent event) {
        super.subAppend(event);
        if (qw != null) {
            long size = ((CountingQuietWriter) qw).getCount();
            if (maxFileSize > 0 && size >= maxFileSize) {
                rollOver();
            }
        }
    }

    void rollOver() {
        closeFile();
        try {
            setFile(generateFileName(), true, bufferedIO, bufferSize);
        } catch (IOException e) {
            if (e instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            LogLog.error("setFile(" + fileName + ", true) call failed.", e);
        }

        deleteOldFiles();
    }

    private void deleteOldFiles() {
        File[] files = new File(folder).listFiles(getLogFileFilter());
        if(files == null)
            return;

        Vector<File> sortedFiles = new Vector<>(new TreeSet<>(Arrays.asList(files)));
        int filesToDelete = Math.max(sortedFiles.size() - maxBackupIndex, 0);
        for (File file : sortedFiles.subList(0, filesToDelete)) {
            if (!file.delete()) {
                LogLog.warn("Could not delete old log file " + file);
            }
        }
    }

    public FilenameFilter getLogFileFilter() {
        return FileFilterUtils.and(FileFilterUtils.prefixFileFilter(prefix), FileFilterUtils.suffixFileFilter(suffix));
    }

    @Override
    protected void setQWForFiles(Writer writer) {
        this.qw = new CountingQuietWriter(writer, errorHandler);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setMaxBackupIndex(int maxBackups) {
        this.maxBackupIndex = maxBackups;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setMaxFileSize(String value) {
        maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setStartupSuffix(String startupSuffix) {
        this.startupSuffix = startupSuffix;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setDateFormat(String dateFormatString) {
        this.dateFormat = new SimpleDateFormat(dateFormatString);
    }
}
