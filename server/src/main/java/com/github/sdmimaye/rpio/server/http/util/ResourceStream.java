package com.github.sdmimaye.rpio.server.http.util;

import com.github.sdmimaye.rpio.server.http.HttpServer;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.management.ManagementFactory;

public class ResourceStream extends InputStream {
    private final InputStream stream;
    static {
        String property = System.getProperty("ui2");
        if(StringUtils.isEmpty(property)){
            SUB_FOLDER = "ui";
            SRC_PATH = "../src/main/resources/" + HttpServer.class.getPackage().getName().replace(".", "/") + "/ui/";
        }else{
            SUB_FOLDER = "ui2";
            SRC_PATH = "../src/main/resources/" + HttpServer.class.getPackage().getName().replace(".", "/") + "/ui2/";
        }
    }

    private static final String SUB_FOLDER;
    private static final String SRC_PATH;

    private static final boolean DEBUGGER_ATTACHED = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("jdwp");

    public ResourceStream(String path) throws IOException {
        InputStream fileStream = getResourceStream(path);
        if (fileStream == null) {
            throw new IllegalArgumentException("Cannot find resource '" + path + "'");
        }

        if (path.endsWith(".includes.js") || path.endsWith(".includes.css")) {
            stream = new IncludeEnabledResourceStream(fileStream, path.substring(0, path.lastIndexOf("/")));
        } else {
            stream = fileStream;
        }
    }

    private InputStream getResourceStream(String normalizedPath) throws FileNotFoundException {
        if (DEBUGGER_ATTACHED) {
            File sourceFile = new File(SRC_PATH + normalizedPath);
            if (sourceFile.exists() && sourceFile.isFile()) {
                return new FileInputStream(sourceFile);
            } else {
                return HttpServer.class.getResourceAsStream(SUB_FOLDER + normalizedPath);
            }
        } else {
            return HttpServer.class.getResourceAsStream(SUB_FOLDER + normalizedPath);
        }
    }

    @Override
    public int read() throws IOException {
        return stream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return stream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return stream.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return stream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return stream.available();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void mark(int readlimit) {
        stream.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        stream.reset();
    }

    @Override
    public boolean markSupported() {
        return stream.markSupported();
    }
}