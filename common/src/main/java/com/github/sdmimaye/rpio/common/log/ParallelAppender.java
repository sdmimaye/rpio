package com.github.sdmimaye.rpio.common.log;

import net.jcip.annotations.ThreadSafe;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

@ThreadSafe
class ParallelAppender extends AppenderWrapper {
    private static final Logger logger = LoggerFactory.getLogger(ParallelAppender.class);
    private final BlockingQueue<LoggingEvent> eventQueue = new LinkedBlockingQueue<>(16384);

    private static final AtomicLong threadCount = new AtomicLong(0);

    private final QueueConsumer queueConsumer;

    public ParallelAppender(Appender wrappedAppender) {
        super(wrappedAppender);

        queueConsumer = new QueueConsumer();
        queueConsumer.start();
    }

    @Override
    public void doAppend(LoggingEvent event) {
        event.getThreadName();
        if(!eventQueue.offer(event))
            logger.warn("Could not append event to queue: " + event);
    }

    private class QueueConsumer extends Thread {
        private QueueConsumer() {
            super("ParallelLogger-" + threadCount.incrementAndGet());
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    wrappedAppender.doAppend(eventQueue.take());
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        queueConsumer.interrupt();
    }
}
