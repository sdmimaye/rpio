package com.github.sdmimaye.rpio.server.http.rest.util;

public class GroupMembershipDetailsGenerator {/*
    private Door door;
    private Person person;
    private String relation;

    public GroupMembershipDetailsGenerator find(Group group) {
        if (door == null && person == null)
            return this;

        if(door != null && person != null)
            return this;

        if (door != null) {
            Set<DoorGroupMembership> collection = door.getDoorGroupMemberships();
            for (DoorGroupMembership dgm : collection) {//search direct groups first
                if (Long.compare(dgm.getGroup().getId(), group.getId()) == 0) {
                    relation = group.getFriendlyName() + " > " + door.getFriendlyName();
                    return this;//only return shortest way
                }
            }
        } else if (person != null) {
            Set<PersonGroupMembership> collection = person.getPersonGroupMemberships();
            for (PersonGroupMembership pga : collection) {
                if(Long.compare(pga.getGroup().getId(), group.getId()) != 0)
                    continue;

                Timeprofile tp = pga.getTimeprofile();
                if(tp == null)
                    return null;

                relation = tp.getFriendlyName();
                return this;
            }
        }

        return this;
    }

    public GroupMembershipDetailsGenerator start(Door door) {
        if (door == null)
            return this;

        this.door = door;
        return this;
    }

    public GroupMembershipDetailsGenerator start(Person person) {
        if (person == null)
            return this;

        this.person = person;
        return this;
    }

    public String getRelation() {
        return relation;
    }*/
}
