package com.github.sdmimaye.rpio.server.http.filter;

import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfo;
import com.github.sdmimaye.rpio.server.util.activeuser.ActiveUserInfoManager;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Provider
public class ResponseLogFilter implements ContainerResponseFilter {
    private static final Logger logger = LoggerFactory.getLogger(ResponseLogFilter.class);
    private final ActiveUserInfoManager activeUserInfoManager;

    @Inject
    public ResponseLogFilter(ActiveUserInfoManager activeUserInfoManager) {
        this.activeUserInfoManager = activeUserInfoManager;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        StringBuilder headerBuffer = new StringBuilder();
        printResponseHeaders(headerBuffer, responseContext.getHeaders());

        logger.info("{} >> STATUS {} | {} byte(s) | '{}' {}", requestContext.getUriInfo().getPath(), responseContext.getStatus(), responseContext.getLength(), getUsername(), headerBuffer);
    }

    private void printResponseHeaders(StringBuilder buffer, MultivaluedMap<String, Object> headers) {
        buffer.append(" [");

        for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
            String header = entry.getKey();
            for (Object value : entry.getValue()) {
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
