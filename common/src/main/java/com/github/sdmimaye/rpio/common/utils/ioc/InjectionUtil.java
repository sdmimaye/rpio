package com.github.sdmimaye.rpio.common.utils.ioc;

import com.google.inject.Inject;
import com.google.inject.Injector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InjectionUtil {
    private final Injector injector;

    @Inject
    public InjectionUtil(Injector injector) {
        this.injector = injector;
    }

    public <T> List<T> getInstances(Collection<Class<? extends T>> classes) {
        List<T> result = new ArrayList<>();
        for (Class<? extends T> aClass : classes) {
            result.add(injector.getInstance(aClass));
        }
        return result;
    }

    public <T> T getInstance(Class<T> type) {
        return injector.getInstance(type);
    }
}
