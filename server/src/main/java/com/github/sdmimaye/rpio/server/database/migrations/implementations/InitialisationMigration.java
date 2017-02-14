package com.github.sdmimaye.rpio.server.database.migrations.implementations;

import com.github.sdmimaye.rpio.server.database.migrations.Migration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Initial-Migration to ensure at least one entry in the migration table.
 * Required to determine wether or not migrations should be performed
 */

public class InitialisationMigration implements Migration {
    @Override
    public String identifier() {
        return "InitialisationMigration from: " + LocalDateTime.of(2014, 4, 2, 14, 31).format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @Override
    public void performUpdate() {
        //do nothing
    }
}
