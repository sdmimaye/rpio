package com.github.sdmimaye.rpio.server.http.rest.models.json.logs;

import java.util.*;

public class JsonActivityLogEntry {
    private Long id;
    private List<String> typeNames = new ArrayList<>();
    private List<Long> primaryKeys = new ArrayList<>();
    private List<Object> before = new ArrayList<>();
    private List<Object> after = new ArrayList<>();
    private String user;
    private Date timestamp;

    public Long getId() {
        return id;
    }

    public List<String> getTypeNames() {
        return typeNames;
    }

    public void setTypeNames(List<String> typeNames) {
        this.typeNames = typeNames;
    }

    public List<Long> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<Long> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void appendBefore(Object before) {
        this.before.add(before);
    }

    public void appendAfter(Object after) {
        this.after.add(after);
    }

    public List<Object> getBefore() {
        return before;
    }

    public void setBefore(List<Object> before) {
        this.before = before;
    }

    public List<Object> getAfter() {
        return after;
    }

    public void setAfter(List<Object> after) {
        this.after = after;
    }
}
