package com.github.sdmimaye.rpio.common.utils.threads;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class NamedThreadFactory implements ThreadFactory {
    private final AtomicLong threadCount = new AtomicLong();
    private final String name;

    public NamedThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, name + "-" + threadCount.getAndIncrement());
    }
}
