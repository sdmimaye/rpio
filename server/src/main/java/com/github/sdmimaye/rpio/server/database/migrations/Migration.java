package com.github.sdmimaye.rpio.server.database.migrations;

public interface Migration{
    String identifier();
    void performUpdate();
}
