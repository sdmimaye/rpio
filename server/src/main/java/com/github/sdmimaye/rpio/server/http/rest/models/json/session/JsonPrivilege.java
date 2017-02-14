package com.github.sdmimaye.rpio.server.http.rest.models.json.session;

public class JsonPrivilege {
    private String name;
    private boolean allowed;

    public JsonPrivilege(String name, boolean allowed) {
        this.name = name;
        this.allowed = allowed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
}
