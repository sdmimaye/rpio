package com.github.sdmimaye.rpio.server.util.os;

public class OsDependantRegistration<T> {
    private final Class<T> from;
    private final Class<? extends T> windows;
    private final Class<? extends T> linux;

    public OsDependantRegistration(Class<T> type, Class<? extends T> windows, Class<? extends T> linux) {
        this.from = type;
        this.windows = windows;
        this.linux = linux;
    }

    public boolean is(Class type) {
        return from.equals(type);
    }

    public Class<T> getFrom() {
        return from;
    }

    public Class<? extends T> getWindows() {
        return windows;
    }

    public Class<? extends T> getLinux() {
        return linux;
    }
}
