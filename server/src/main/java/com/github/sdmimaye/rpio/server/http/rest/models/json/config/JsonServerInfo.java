package com.github.sdmimaye.rpio.server.http.rest.models.json.config;

public class JsonServerInfo {
    public JsonServerInfo() {
    }

    public JsonServerInfo(String address, int port) {
        this.address = address;
        this.port = port;
    }

    private String address;
    private int port;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
