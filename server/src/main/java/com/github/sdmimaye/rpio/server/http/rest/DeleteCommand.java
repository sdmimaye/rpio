package com.github.sdmimaye.rpio.server.http.rest;

import java.util.HashSet;
import java.util.Set;

public class DeleteCommand<T>{
    private Set<T> entries = new HashSet<>();
    private Set<String> deletedRelations = new HashSet<>();
    private Set<String> removedRelations = new HashSet<>();

    public Set<String> getDeletedRelations() {
        return deletedRelations;
    }

    public void setDeletedRelations(Set<String> deletedRelations) {
        this.deletedRelations = deletedRelations;
    }

    public Set<String> getRemovedRelations() {
        return removedRelations;
    }

    public void setRemovedRelations(Set<String> removedRelations) {
        this.removedRelations = removedRelations;
    }

    public Set<T> getEntries() {
        return entries;
    }

    public void setEntries(Set<T> entries) {
        this.entries = entries;
    }
}
