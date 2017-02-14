package com.github.sdmimaye.rpio.server.database.recovery;

import java.io.File;
import java.io.InputStream;

public interface DatabaseBackupGenerator {
    String createBackup(String host, String database, String username, String password, String backupPassword);
    File getBackup(String host, String database, String username, String password, String request);
    void restore(String host, String database, String username, String password, String filename, String backupPassword);
    String setBackup(String host, String database, String username, String password, InputStream stream);
}
