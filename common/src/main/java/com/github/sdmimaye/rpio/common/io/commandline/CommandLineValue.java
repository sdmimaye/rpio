package com.github.sdmimaye.rpio.common.io.commandline;

public class CommandLineValue extends CommandLineArg {
    private final String value;

    public CommandLineValue(String name, String value) {
        super(name);
        this.value = value;
    }

    public String getRawValue() {
        return value;
    }

    public boolean asBoolean(){
        return Boolean.valueOf(value);
    }

    public int asInt32(){
        return Integer.parseInt(value);
    }

    public long asInt64(){
        return Long.parseLong(value);
    }

    public String asString(){
        return value;
    }
}
