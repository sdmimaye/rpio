package com.github.sdmimaye.rpio.server.http.rest.models.json.base;

public class JsonObject {
    private Long id;
    private String uuid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        JsonObject instance = obj instanceof JsonObject ? (JsonObject) obj : null;
        if(instance == null) return false;

        return instance.getId().equals(getId());
    }
}
