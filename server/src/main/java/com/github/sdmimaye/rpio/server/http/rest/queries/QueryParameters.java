package com.github.sdmimaye.rpio.server.http.rest.queries;

import com.github.sdmimaye.rpio.server.http.rest.OrderByDirection;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.QueryParam;

public class QueryParameters {
    @QueryParam("offset")
    private int offset;

    @QueryParam("count")
    private int count;

    @QueryParam("filter")
    private String filter;

    @QueryParam("orderBy")
    private String orderBy;

    @QueryParam("orderByDirection")
    private OrderByDirection orderByDirection = OrderByDirection.ASC;

    @QueryParam("additional")
    private QueryParameterCollection additional;

    public QueryParameters() {
        this.offset = 0;
        this.count = Integer.MAX_VALUE;
        this.filter = null;
    }

    public static QueryParameters generate(int count, int offset, String orderBy, String filter) {
        return generate(count, offset, orderBy, OrderByDirection.ASC, filter);
    }

    public static QueryParameters generate(int count, int offset, String orderBy, OrderByDirection direction, String filter) {
        QueryParameters query = new QueryParameters();
        query.setOffset(offset);
        query.setCount(count);
        query.setFilter(filter);
        query.setOrderBy(orderBy);
        query.setOrderByDirection(direction);

        return query;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isOrdered() {
        return StringUtils.isNotBlank(orderBy);
    }

    public boolean isShowAll() {
        return StringUtils.isBlank(filter);
    }

    public OrderByDirection getOrderByDirection() {
        return orderByDirection;
    }

    public void setOrderByDirection(OrderByDirection orderByDirection) {
        this.orderByDirection = orderByDirection;
    }

    public QueryParameterCollection getAdditional() {
        return additional;
    }

    public void setAdditional(QueryParameterCollection additional) {
        this.additional = additional;
    }

    public String getAdditionalByName(String name) {
        return this.additional == null ? null : this.additional.getByName(name);
    }
}
