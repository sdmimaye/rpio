package com.github.sdmimaye.rpio.server.http.util;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;

public class IncludeEnabledResourceStream extends InputStream {
    private static final Logger logger = LoggerFactory.getLogger(IncludeEnabledResourceStream.class);

    private final BufferedReader reader;
    private final SequenceInputStream stream;

    public IncludeEnabledResourceStream(InputStream fileStream, final String basePath) throws IOException {
        reader = new BufferedReader(new InputStreamReader(fileStream, Charsets.UTF_8));
        stream = new SequenceInputStream(new Enumeration<InputStream>() {
            String nextLine = findNextLine();

            String findNextLine() {
                try {
                    String nextLine;
                    do {
                        nextLine = reader.readLine();
                    }
                    while (nextLine != null && //
                            (nextLine.isEmpty() || nextLine.startsWith("#") || nextLine.startsWith("//")));

                    return nextLine;
                } catch (IOException e) {
                    logger.warn("Error while parsing include file", e);
                    return null;
                }
            }

            @Override
            public boolean hasMoreElements() {
                return nextLine != null;
            }

            @Override
            public InputStream nextElement() {
                InputStream resourceStream;
                try {
                    String path = basePath + "/" + nextLine;
                    resourceStream = new SequenceInputStream(new ByteArrayInputStream(("\n\n/*\n     " + path + "\n*/\n\n").getBytes("UTF-8")), new ResourceStream(path));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                nextLine = findNextLine();
                return resourceStream;
            }
        });
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
    public void close() throws IOException {
        IOUtils.closeQuietly(reader);
        IOUtils.closeQuietly(stream);
    }
}
