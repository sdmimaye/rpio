package com.github.sdmimaye.rpio.server.database.models.gpio;

import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;
import com.github.sdmimaye.rpio.server.database.models.enums.PinLogic;
import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import com.github.sdmimaye.rpio.server.database.models.enums.PinOuputMode;
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

    @Enumerated(EnumType.STRING)
    private PinLogic logic;

    @Enumerated(EnumType.STRING)
    private PinOuputMode ouputMode;

    private Integer timeout;

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

    public PinOuputMode getOuputMode() {
        return ouputMode;
    }

    public void setOuputMode(PinOuputMode ouputMode) {
        this.ouputMode = ouputMode;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public PinLogic getLogic() {
        return logic;
    }

    public void setLogic(PinLogic logic) {
        this.logic = logic;
    }
}
