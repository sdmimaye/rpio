package com.github.sdmimaye.rpio.server.http.rest.models.json.gpio;

import com.github.sdmimaye.rpio.server.database.models.enums.PinMode;
import com.github.sdmimaye.rpio.server.database.models.gpio.Pin;
import com.github.sdmimaye.rpio.server.database.models.validation.readable.ReadablePin;
import com.github.sdmimaye.rpio.server.http.rest.models.json.base.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class JsonPin extends JsonObject implements ReadablePin {
    private Integer number;
    private PinMode mode;

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

    public static JsonPin convert(Pin pin) {
        JsonPin result = new JsonPin();
        result.setId(pin.getId());
        result.setUuid(pin.getUuid());
        result.setNumber(pin.getNumber());
        result.setMode(pin.getMode());

        return result;
    }

    public static List<JsonPin> convert(List<Pin> list) {
        return list.stream().map(p -> JsonPin.convert(p)).collect(Collectors.toList());
    }
}
