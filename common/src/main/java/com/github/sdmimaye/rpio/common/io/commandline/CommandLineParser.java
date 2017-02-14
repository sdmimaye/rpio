package com.github.sdmimaye.rpio.common.io.commandline;

import java.util.List;

public class CommandLineParser {
    public static CommandLine doParseCommandLine(String[] args) {
        CommandLine result = new CommandLine();
        for (String arg : args) {
            if(arg.startsWith("--")){
                doParseLongOption(arg, result);
            }else if(arg.startsWith("-")){
                doParseShortOption(arg, result);
            }else{
                throw new RuntimeException("Could not parse Commandline-Option: " + arg + ". Options start with either - or --");
            }
        }

        return result;
    }

    private static void doParseShortOption(String option, List<CommandLineArg> result) {
        option = option.substring(1);
        doHandleUnifiedOption(option, result);
    }

    private static void doParseLongOption(String option, List<CommandLineArg> result) {
        option = option.substring(2);
        doHandleUnifiedOption(option, result);
    }

    private static void doHandleUnifiedOption(String option, List<CommandLineArg> result) {
        if(option.contains("=")){
            doParseKeyValueOption(option, result);
        }else{
            doParseCommandLineOption(option, result);
        }
    }

    private static void doParseCommandLineOption(String option, List<CommandLineArg> result) {
        result.add(new CommandLineOption(option));
    }

    private static void doParseKeyValueOption(String option, List<CommandLineArg> result) {
        String[] kvp = option.split("=");
        if(kvp.length != 2)
            throw new RuntimeException("Invalid Commandline-Option: " + option);

        result.add(new CommandLineValue(kvp[0], kvp[1]));
    }
}
