package com.github.sdmimaye.rpio.server.database.models.gpio;

import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;
import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Audited
public class GpioPin extends PersistedEntityBase {
    private Integer number;

    @Enumerated(EnumType.STRING)
    private PinMode mode;

    private String description;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public PinMode getMode() {
        return mode;
    }

    public void setMode(PinMode mode) {
        this.mode = mode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
