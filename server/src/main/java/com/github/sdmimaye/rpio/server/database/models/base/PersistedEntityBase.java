package com.github.sdmimaye.rpio.server.database.models.base;

import com.sun.istack.internal.NotNull;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
public abstract class PersistedEntityBase {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String uuid = UUID.randomUUID().toString();

    public Long getId() {
        return id;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    private static void doValidateUuidOrThrow(PersistedEntityBase p){
        if(p.uuid == null) throw new RuntimeException("Invalid Entity without UUID found. ID: " + p.id);
    }

    @Override
    public final boolean equals(Object o) {
        if(this == o) return true;
        PersistedEntityBase base = o instanceof PersistedEntityBase ? (PersistedEntityBase) o : null;
        if(base == null) return false;

        doValidateUuidOrThrow(this);
        doValidateUuidOrThrow(base);

        return uuid.equals(base.uuid);
    }

    @Override
    public final int hashCode() {
        doValidateUuidOrThrow(this);
        return uuid.hashCode();
    }
}
