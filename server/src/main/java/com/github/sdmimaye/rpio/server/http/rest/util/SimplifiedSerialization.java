package com.github.sdmimaye.rpio.server.http.rest.util;

import com.github.sdmimaye.rpio.server.database.models.base.PersistedEntityBase;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

public class SimplifiedSerialization {
    private SimplifiedSerialization() {

    }

    private static final Class collection = Collection.class;
    private static final Class dbType = PersistedEntityBase.class;

    public static HashMap<String, Object> simplify(Object object) throws IllegalAccessException, InvocationTargetException {
        HashMap<String, Object> result = new HashMap<>();
        if (object == null)
            return null;

        Class c = object.getClass();
        Method[] methods = c.getMethods();
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            Class fieldType = field.getType();
            Method selector = getMethodForField(field, methods);
            if (selector == null)
                continue;

            Object value = selector.invoke(object);
            if (fieldType.isAssignableFrom(collection) || collection.isAssignableFrom(fieldType)) {//collection -> just take the size
                Collection col = (Collection) value;
                result.put(field.getName(), String.valueOf(col.size()));
            } else if (fieldType.isAssignableFrom(dbType) || dbType.isAssignableFrom(fieldType)) {//skip complex types
//                HashMap<String, Object> simplified = simplify(value);
//                result.put(field.getName(), simplified);
            } else {
                result.put(field.getName(), value == null ? null : value.toString());
            }
        }

        return result;
    }

    private static Method getMethodForField(Field field, Method[] methods) {
        for (Method method : methods) {
            if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase()) && method.getName().startsWith("get"))
                return method;
        }

        return null;
    }
}
