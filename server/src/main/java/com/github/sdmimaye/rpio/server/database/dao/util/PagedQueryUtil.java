package com.github.sdmimaye.rpio.server.database.dao.util;

import com.github.sdmimaye.rpio.server.http.rest.queries.QueryParameters;
import com.github.sdmimaye.rpio.server.util.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PagedQueryUtil {
    private PagedQueryUtil() {
    }

    public static Long getCount(Session session, QueryParameters query, String tableName, List<String> filterQueries) {
        Long result = 0L;
        for (String filter : filterQueries) {
            result += getCount(session, query, tableName, filter);
        }

        return result;
    }

    public static <T> List<T> getPaged(Session session, QueryParameters query, String tableName, List<String> filterQueries) {
        ArrayList<T> allResults = new ArrayList<T>();
        for (String filterQuery : filterQueries) {
            int existingResults = allResults.size();

            List<T> filterResults = getResults(session, query, tableName, filterQuery, query.getCount() - existingResults, Math.max(query.getOffset() - existingResults, 0));

            allResults.addAll(filterResults);

            if (allResults.size() >= query.getCount()) {
                break;
            }
        }

        return allResults;
    }

    private static Long getCount(Session session, QueryParameters queryParams, String tableName, String filter) {
        String queryString = "select count(*) as cnt from " + tableName + " " + filter;

        String orderBy = SqlUtil.escape(queryParams.getOrderBy());
        if (StringUtils.isNotEmpty(orderBy)) {
            queryString += " order by " + orderBy;
        }

        Query query = session.createQuery(queryString);
        String filterString = queryParams.getFilter() == null ? "" : queryParams.getFilter();

        if (queryString.contains(":prefixFilter")) {
            query.setParameter("prefixFilter", filterString + "%");
        }

        if (queryString.contains(":filter")) {
            query.setParameter("filter", "%" + filterString + "%");
        }

        return (Long) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> getResults(Session session, QueryParameters queryParams, String tableName, String filterQuery, int count, int offset) {
        if (offset < 0 || count < 0)
            return Collections.emptyList();

        String queryString = "from " + tableName;
        queryString += " " + filterQuery;

        String orderBy = SqlUtil.escape(queryParams.getOrderBy());
        if (StringUtils.isNotEmpty(orderBy)) {
            queryString += " order by " + orderBy;
        }

        Query query = session.createQuery(queryString)
                .setFirstResult(offset)
                .setMaxResults(count);
        String filterString = queryParams.getFilter() == null ? "" : queryParams.getFilter();

        if (queryString.contains(":prefixFilter")) {
            query.setParameter("prefixFilter", filterString + "%");
        }

        if (queryString.contains(":filter")) {
            query.setParameter("filter", "%" + filterString + "%");
        }
        return (List<T>) query.list();
    }
}
