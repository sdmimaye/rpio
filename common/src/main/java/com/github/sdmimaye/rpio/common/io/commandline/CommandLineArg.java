package com.github.sdmimaye.rpio.common.io.commandline;

public abstract class CommandLineArg {
    private final String name;

    protected CommandLineArg(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
