package com.github.sdmimaye.rpio.server.database.models.system;

import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;
import com.github.sdmimaye.rpio.server.database.models.enums.MigrationType;

import javax.persistence.*;
import java.util.Date;

@Entity
public class MigrationHistory extends PersistedEntityBase {
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    private String identifier;

    @Enumerated(EnumType.STRING)
    private MigrationType type;

    public MigrationHistory() {

    }

    public MigrationHistory(String identifier, MigrationType type) {
        this.identifier = identifier;
        this.type = type;
        this.timestamp = new Date();//now
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public MigrationType getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setType(MigrationType type) {
        this.type = type;
    }
}
