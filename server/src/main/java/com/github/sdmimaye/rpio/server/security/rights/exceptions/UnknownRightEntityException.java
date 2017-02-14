package com.github.sdmimaye.rpio.server.security.rights.exceptions;

public class UnknownRightEntityException extends UserRightsException {
    public UnknownRightEntityException(String entityName){
        super("The Entity: " + entityName + " is unknown");
    }
}
