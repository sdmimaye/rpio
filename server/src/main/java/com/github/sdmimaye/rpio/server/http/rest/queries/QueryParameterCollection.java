package com.github.sdmimaye.rpio.server.http.rest.queries;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class QueryParameterCollection {
    private final Map<String, String> map;
    public QueryParameterCollection(String json) {
        Map<String, String> map;
        try {
            ObjectMapper mapper = new ObjectMapper();
            map = mapper.readValue(json, HashMap.class);
        } catch (Exception ex) {
            map = new HashMap<>();
        }

        this.map = map;
    }

    public String getByName(String name) {
        return map.get(name);
    }
}
