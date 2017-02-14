package com.github.sdmimaye.rpio.server.database.dao.base;

import com.github.sdmimaye.rpio.server.http.rest.queries.QueryParameterCollection;
import com.github.sdmimaye.rpio.server.http.rest.queries.QueryParameters;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class HibernateDaoBase<T> extends HibernateSessionHolder implements StandardDao<T> {
    @Inject
    private Injector injector;

    public HibernateDaoBase(Provider<Session> session) {
        super(session);
    }

    @Override
    public final void save(T object) {
        session.get().save(object);
    }

    @Override
    public void saveOrUpdate(T object) {
        session.get().saveOrUpdate(object);
    }

    @Override
    public void delete(T object) {
        session.get().delete(object);
    }

    @Override
    public final T getById(Long id) {
        if (id == null) {
            return null;
        }

        Class<T> clazz = getModelClass();
        return clazz.cast(session.get().get(clazz, id));
    }

    protected final Criteria buildCriteria(QueryParameters queryParameters) {
        Criteria criteria = createCriteria();
        processAdditionalToCriteria(criteria, queryParameters.getAdditional());

        if (queryParameters.isOrdered()) {
            switch (queryParameters.getOrderByDirection()) {
                case ASC:
                    criteria.addOrder(Order.asc(queryParameters.getOrderBy()).ignoreCase());
                    break;
                case DESC:
                    criteria.addOrder(Order.desc(queryParameters.getOrderBy()).ignoreCase());
                    break;
            }
        }

        ArrayList<Criterion> allFilters = new ArrayList<>();
        String[] filterStrings = queryParameters.getFilter().split(" ");
        for (String filter : filterStrings) {
            filter = filter.trim();

            ArrayList<Criterion> filterRestrictions = new ArrayList<>();
            for (Field field : getModelClass().getDeclaredFields()) {
                if (field.getType() == String.class) {
                    filterRestrictions.add(Restrictions.ilike(field.getName(), filter, MatchMode.ANYWHERE));
                } else {
                    filterRestrictions.addAll(forFieldWithFilter(criteria, field, filter));
                }
            }

            allFilters.add(Restrictions.or(filterRestrictions.toArray(new Criterion[filterRestrictions.size()])));
        }
        criteria.add(Restrictions.and(allFilters.toArray(new Criterion[allFilters.size()])));

        return criteria;
    }

    protected List<Criterion> forFieldWithFilter(Criteria criteria, Field field, String filter) {
        return Collections.emptyList();
    }

    protected void processAdditionalToCriteria(Criteria criteria, QueryParameterCollection additional) {
        //point for clients to capture criteria and manipulate if required
    }

    @SuppressWarnings("unchecked")
    public final List<T> getByQueryParameters(QueryParameters parameters) {
        return buildCriteria(parameters).setMaxResults(parameters.getCount()).setFirstResult(parameters.getOffset()).list();
    }

    public final long getCountByQueryParameters(QueryParameters parameters) {
        return ((Number) buildCriteria(parameters).setProjection(Projections.count("id")).uniqueResult()).longValue();
    }

    public final ScrollableResults getPagedIterableQueryParameters(QueryParameters query) {
        return buildCriteria(query).scroll(ScrollMode.FORWARD_ONLY);
    }

    protected final Criteria createCriteria() {
        return session.get().createCriteria(getModelClass());
    }

    @Override
    @SuppressWarnings("unchecked")
    public final T getByUuid(String uuid) {
        return (T) createCriteria().add(Restrictions.eq("uuid", uuid)).uniqueResult();
    }

    protected abstract Class<T> getModelClass();

    @Override
    public final T getById(String id) {
        try {
            return getById(Long.valueOf(id));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public final List<T> getAll() {
        return createCriteria(getModelClass()).list();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public List<Long> getAllIds() {
        return createCriteria(getModelClass()).setProjection(Property.forName("id")).list();
    }

    public List<Object> getAllProperties(String name) {
        return createCriteria(getModelClass()).setProjection(Property.forName(name)).list();
    }

    @Override
    public long getTotalCount() {
        return (Long) session.get().createCriteria(getModelClass()).setProjection(Projections.rowCount()).uniqueResult();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public List<T> getAllExceptFor(List<Long> excludedIds) {
        List<T> returnedEntities = new ArrayList<T>();

        List<Long> idsOfEntitiesToReturn = getAllIds();
        idsOfEntitiesToReturn.removeAll(excludedIds);

        for (Long id : idsOfEntitiesToReturn) {
            T entity = (T) session.get().get(getModelClass(), id);
            returnedEntities.add(entity);
        }

        return returnedEntities;
    }

    @Override
    public void update(T object) {
        session.get().update(object);
    }

    @Override
    public void evict(T object) {
        session.get().evict(object);
    }

    @Override
    public void merge(T object) {
        session.get().merge(object);
    }

    @Override
    public T create() {
        return injector.getInstance(getModelClass());
    }
}
