package com.github.sdmimaye.rpio.server.http.rest.models.json.gpio;

import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import com.github.sdmimaye.rpio.server.database.models.gpio.GpioPin;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadableGpioPin;
import com.github.sdmimaye.rpio.server.http.rest.models.json.base.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class JsonGpioGpioPin extends JsonObject implements ReadableGpioPin {
    private Integer number;
    private PinMode mode;
    private String description;

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
        result.setDescription(pin.getDescription());

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
}
