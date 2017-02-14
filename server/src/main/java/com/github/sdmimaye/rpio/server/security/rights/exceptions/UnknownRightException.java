package com.github.sdmimaye.rpio.server.security.rights.exceptions;

public class UnknownRightException extends UserRightsException {
    public UnknownRightException(String rightName){
        super("The Right: " + rightName + " is unknown. Please use either: none, read, write, delete or all");
    }
}
