package com.github.sdmimaye.rpio.server.http.util;

import com.google.inject.Singleton;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ResourceServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ResourceServlet.class);

    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put(".css", "text/css");
        MIME_TYPES.put(".html", "text/html");
        MIME_TYPES.put(".htm", "text/html");
        MIME_TYPES.put(".js", "application/x-javascript");
        MIME_TYPES.put(".png", "image/png");
        MIME_TYPES.put(".ico", "image/x-icon");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        InputStream resourceStream = null;
        ServletOutputStream outputStream = null;

        try {
            String path = request.getPathInfo();
            if (StringUtils.equals(path, "/")) path = "/index.html";
            URI uri = new URI(path);
            String normalizedPath = uri.normalize().getPath();

            resourceStream = new ResourceStream(normalizedPath);
            outputStream = response.getOutputStream();

            int dotIndex = normalizedPath.lastIndexOf(".");
            if (dotIndex != -1) {
                String extension = normalizedPath.substring(dotIndex);
                String contentType = MIME_TYPES.get(extension);
                if (contentType != null) {
                    response.setContentType(contentType);
                }
            }

            logger.debug("{} => '{}'", request.getRemoteAddr(), normalizedPath);
            IOUtils.copy(resourceStream, outputStream);
            outputStream.flush();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(resourceStream);
            IOUtils.closeQuietly(outputStream);
        }
    }

}
