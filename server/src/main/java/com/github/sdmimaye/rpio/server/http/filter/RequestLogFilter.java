package com.github.sdmimaye.rpio.server.http.filter;

import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfo;
import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfoManager;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Provider
public class RequestLogFilter implements ContainerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLogFilter.class);
    private final ActiveUserInfoManager activeUserInfoManager;

    @Inject
    public RequestLogFilter(ActiveUserInfoManager activeUserInfoManager) {
        this.activeUserInfoManager = activeUserInfoManager;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final StringBuilder headerBuffer = new StringBuilder();
        printRequestHeaders(headerBuffer, requestContext.getHeaders());
        logger.info("<< {} {} | {} byte(s) | '{}' {}", requestContext.getMethod(), requestContext.getUriInfo().getPath(), requestContext.getLength(), getUsername(), headerBuffer);
    }

    private void printRequestHeaders(StringBuilder buffer, MultivaluedMap<String, String> headers) {
        buffer.append("[");

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String header = entry.getKey();
            for (String value : entry.getValue()) {
                buffer.append(header).append(": ").append(value).append(" // ");
            }
        }

        buffer.append("]");
    }

    private String getUsername() {
        ActiveUserInfo info = activeUserInfoManager.getInfo();
        if (info == null || info.getUserName() == null) {
            return "unknown user";
        } else {
            return info.getUserName();
        }
    }
}
