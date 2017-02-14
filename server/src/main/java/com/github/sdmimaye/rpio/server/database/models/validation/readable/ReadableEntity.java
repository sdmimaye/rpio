package com.github.sdmimaye.rpio.server.database.models.validation.readable;

public interface ReadableEntity extends Readable{
    Long getId();
    String getUuid();
}
