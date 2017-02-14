package com.github.sdmimaye.rpio.server.security.rights.exceptions;

public class RightMalformedException extends UserRightsException {
    public RightMalformedException(String rightString){
        super("The String: " + rightString + " is malformed. Please use the format: 'entity-access' ie: 'person-read'");
    }
}
