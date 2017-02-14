package com.github.sdmimaye.rpio.server.database.recovery.hsql;

import com.github.sdmimaye.rpio.server.database.recovery.DatabaseBackupGenerator;
import com.github.sdmimaye.rpio.server.database.recovery.exceptions.DatabaseRecoveryException;
import com.github.sdmimaye.rpio.server.util.io.ZipUtils;
import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class HsqlDatabaseBackupGenerator implements DatabaseBackupGenerator {
    private final ZipUtils utils = new ZipUtils();

    @Inject
    public HsqlDatabaseBackupGenerator(String host) {
    }

    private File directory(String host){
        File databaseDirectory = new File(host);
        if(!databaseDirectory.exists())
            throw new RuntimeException("Database Folder not found");

        File backups = new File(databaseDirectory, "../db-backups");
        if(!backups.exists() && !backups.mkdir())
            throw new RuntimeException("Error while generating Database-Backup directory");

        return backups;
    }

    @Override
    public String createBackup(String host, String database, String username, String password, String backupPassword) {
        File databaseDirectory = new File(host);
        File backups = directory(host);
        String request = UUID.randomUUID().toString();
        File output = new File(backups, request + ".zip");
        try {
            utils.zip(databaseDirectory.getAbsolutePath(), output.getAbsolutePath(), backupPassword);
            return request;
        } catch (IOException ioe) {
            throw new RuntimeException("Error while generating Zip-File", ioe);
        }
    }

    @Override
    public File getBackup(String host, String database, String username, String password, String request) {
        File backups = directory(host);
        File output = new File(backups, request + ".zip");
        if(!output.exists())
            throw new RuntimeException("Request File not found");

        return output;
    }

    @Override
    public void restore(String host, String database, String username, String password, String request, String backupPassword) {
        File databaseDirectory = new File(host);
        File backups = directory(host);
        File selected = new File(backups, request + ".zip");
        if(!selected.exists())
            throw new RuntimeException("Backup-File not found");

        File databaseParent = databaseDirectory.getParentFile();
        try {
            utils.unzip(selected.getAbsolutePath(), databaseParent.getAbsolutePath(), backupPassword);
        } catch (IOException ioe) {
            throw DatabaseRecoveryException.determine(ioe);
        }
    }

    @Override
    public String setBackup(String host, String database, String username, String password, InputStream source) {
        File backups = directory(host);
        String request = UUID.randomUUID().toString();
        String filename = request + ".zip";
        File current = new File(backups, filename);
        try {
            FileOutputStream target = new FileOutputStream(current, false);
            IOUtils.copy(source, target);
            IOUtils.closeQuietly(target);

            return request;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
