package com.github.sdmimaye.rpio.server.http.rest.models.json.gpio;

import com.github.sdmimaye.rpio.server.database.models.enums.PinLogic;
import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import com.github.sdmimaye.rpio.server.database.models.enums.PinOuputMode;
import com.github.sdmimaye.rpio.server.database.models.gpio.GpioPin;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadableGpioPin;
import com.github.sdmimaye.rpio.server.http.rest.models.json.base.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class JsonGpioGpioPin extends JsonObject implements ReadableGpioPin {
    private Integer number;
    private PinMode mode;
    private PinLogic logic;
    private String description;
    private PinOuputMode ouputMode;
    private Integer timeout;

    @Override
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public PinMode getMode() {
        return mode;
    }

    public void setMode(PinMode mode) {
        this.mode = mode;
    }

    public static JsonGpioGpioPin convert(GpioPin pin) {
        JsonGpioGpioPin result = new JsonGpioGpioPin();
        result.setId(pin.getId());
        result.setUuid(pin.getUuid());
        result.setNumber(pin.getNumber());
        result.setMode(pin.getMode());
        result.setLogic(pin.getLogic());
        result.setDescription(pin.getDescription());
        result.setOuputMode(pin.getOuputMode());
        result.setTimeout(pin.getTimeout());

        return result;
    }

    public static List<JsonGpioGpioPin> convert(List<GpioPin> list) {
        return list.stream().map(p -> JsonGpioGpioPin.convert(p)).collect(Collectors.toList());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public PinOuputMode getOuputMode() {
        return ouputMode;
    }

    public void setOuputMode(PinOuputMode ouputMode) {
        this.ouputMode = ouputMode;
    }

    @Override
    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    @Override
    public PinLogic getLogic() {
        return logic;
    }

    public void setLogic(PinLogic logic) {
        this.logic = logic;
    }
}
