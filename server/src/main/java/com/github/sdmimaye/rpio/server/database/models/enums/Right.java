package com.github.sdmimaye.rpio.server.database.models.enums;


public enum Right{
    NONE(0),
    READ(1),
    WRITE(READ.getStatusFlagValue() | 1<<1),
    DELETE(WRITE.getStatusFlagValue() | 1<<2),
    ALL(READ.getStatusFlagValue() | WRITE.getStatusFlagValue() | DELETE.getStatusFlagValue());
    private final long statusFlagValue;

    Right(long statusFlagValue) {
        this.statusFlagValue = statusFlagValue;
    }

    public long getStatusFlagValue(){
        return statusFlagValue;
    }

    public boolean isAllowed(Right right) {
        return (right.getStatusFlagValue() & this.getStatusFlagValue()) == right.getStatusFlagValue();
    }
}