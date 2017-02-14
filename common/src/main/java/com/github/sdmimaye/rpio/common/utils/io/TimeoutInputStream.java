package com.github.sdmimaye.rpio.common.utils.io;

import com.github.sdmimaye.rpio.common.utils.threads.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.concurrent.*;

public class TimeoutInputStream extends FilterInputStream {
    private static final Logger logger = LoggerFactory.getLogger(TimeoutInputStream.class);

    private static final ScheduledExecutorService TIMER = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("TimeoutInputStream"));
    private final Class<? extends IOException> timeoutExceptionClass;
    private long timeoutMillis;
    private volatile boolean timedOut;

    public TimeoutInputStream(InputStream inputStream,
                              long timeoutMillis,
                              Class<? extends IOException> timeoutExceptionClass) {
        super(inputStream);
        this.timeoutMillis = timeoutMillis;
        this.timeoutExceptionClass = timeoutExceptionClass;

        try {
            timeoutExceptionClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private ScheduledFuture<?> startTimeout(final Thread workerThread) {
        timedOut = false;

        Runnable task = () -> {
            try {
                timedOut = true;
                workerThread.interrupt();
            } catch (Throwable t) {
                logger.info("Error while executing timeout task", t);
            }
        };

        return TIMER.schedule(task, timeoutMillis, TimeUnit.MILLISECONDS);
    }

    private <T> T performAction(Callable<T> action) throws IOException {
        ScheduledFuture<?> timerTask = startTimeout(Thread.currentThread());
        try {
            return action.call();
        } catch (InterruptedIOException interruptedException) {
            if (timedOut) {
                IOException timeoutException;
                try {
                    timeoutException = timeoutExceptionClass.newInstance();
                } catch (Exception instantiationException) {
                    throw new IOException(instantiationException);
                }

                throw timeoutException;
            } else {
                throw interruptedException;
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            timerTask.cancel(true);
        }
    }

    @Override
    public int read() throws IOException {
        return performAction(() -> in.read());
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return performAction(() -> in.read(b));
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return performAction(() -> in.read(b, off, len));
    }

    @Override
    public long skip(final long n) throws IOException {
        return performAction(() -> in.skip(n));
    }

    @Override
    public int available() throws IOException {
        return performAction(() -> in.available());
    }

    @Override
    public void close() throws IOException {
        performAction(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                in.close();
                return null;
            }
        });
    }

    @Override
    public synchronized void mark(final int readlimit) {
        try {
            performAction(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    in.mark(readlimit);
                    return null;
                }
            });
        } catch (IOException ignored) {
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        performAction(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                in.reset();
                return null;
            }
        });
    }
}
