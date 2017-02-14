package com.github.sdmimaye.rpio.server.database.hibernate;

public interface HibernateAction<T> {
    T call();
}
