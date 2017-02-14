package com.github.sdmimaye.rpio.common.io.commandline;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Optional;

public class CommandLine extends ArrayList<CommandLineArg>{
    private String doShortenOption(String option) {
        if(option.startsWith("--"))
            return option.substring(2);
        else if(option.startsWith("-"))
            return option.substring(1);
        return option;
    }

    public boolean hasOption(String option) {
        final String shorten = doShortenOption(option);
        return stream().anyMatch(o -> (o instanceof CommandLineOption) && StringUtils.equalsIgnoreCase(o.getName(), shorten));
    }

    public CommandLineValue getValueIfExisting(String name) {
        final String shorten = doShortenOption(name);
        Optional<CommandLineArg> first = stream().filter(o -> (o instanceof CommandLineValue) && StringUtils.equalsIgnoreCase(o.getName(), shorten)).findFirst();
        if(first.isPresent())
            return (CommandLineValue) first.get();

        return null;
    }
}
