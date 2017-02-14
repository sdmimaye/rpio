package com.github.sdmimaye.rpio.common.utils.io.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public class SocketTimeoutClosure implements Closeable {
    private final Socket socket;
    private final int initialTimeoutValue;

    public SocketTimeoutClosure(int blockTimeoutValue, Socket socket) throws IOException {
        this.socket = socket;
        this.initialTimeoutValue = socket.getSoTimeout();
        this.socket.setSoTimeout(blockTimeoutValue);
    }

    @Override
    public void close() throws IOException {
        this.socket.setSoTimeout(initialTimeoutValue);
    }
}
