package com.github.sdmimaye.rpio.server.http.rest.models.json.logs;

import java.io.File;
import java.util.Date;

public class JsonLogFile {
    private long length;
    private Date lastModified;
    private String filename;

    public JsonLogFile(File file) {
        filename = file.getName();
        this.length = file.length();
        lastModified = new Date(file.lastModified());
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
