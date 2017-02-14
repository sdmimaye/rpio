package com.github.sdmimaye.rpio.server.database.dao.base;

import java.util.List;

public interface StandardDao<T> {
    /**
     * Creates a new instance of the managed entity. Returns a detached instance which is not yet persisted.
     */
    T create();

    void save(T object);

    void saveOrUpdate(T object);

    void delete(T object);

    void update(T object);

    void evict(T object);

    void merge(T object);

    T getById(Long id);

    T getById(String id);

    T getByUuid(String id);

    List<T> getAll();

    List<Long> getAllIds();

    long getTotalCount();

    List<T> getAllExceptFor(List<Long> ids);
}
