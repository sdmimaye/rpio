package com.github.sdmimaye.rpio.server.http.rest.models.json.logs;

import com.github.sdmimaye.rpio.common.utils.version.VersionUtil;
import org.apache.commons.lang3.SystemUtils;

import java.lang.management.ManagementFactory;
import java.util.Date;

public class JsonServerStatus {
    private final Date start;
    private final Date time;
    private final String javaVersion;
    private final String serverVersion;

    public JsonServerStatus() {
        start = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
        time = new Date();
        javaVersion = SystemUtils.JAVA_RUNTIME_NAME + " (build " + SystemUtils.JAVA_RUNTIME_VERSION + ")";
        serverVersion = VersionUtil.getVersionString(JsonServerStatus.class);
    }

    public Date getStart() {
        return start;
    }

    public Date getTime() {
        return time;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getServerVersion() {
        return serverVersion;
    }
}
