package com.github.sdmimaye.rpio.server.util.os;

import com.github.sdmimaye.rpio.common.utils.ioc.InjectionUtil;
import com.github.sdmimaye.rpio.common.utils.state.OsUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class OsDependantInjector {
    private final InjectionUtil util;
    private final List<OsDependantRegistration> registrations = new ArrayList<>();

    @Inject
    public OsDependantInjector(InjectionUtil util) {
        this.util = util;
    }

    public <T> OsDependantInjector register(Class<T> from, Class<? extends T> windows, Class<? extends T> linux) {
        registrations.add(new OsDependantRegistration(from, windows, linux));
        return this;
    }

    public <T> T resolve(Class<T> type){
        for (OsDependantRegistration registration : registrations) {
            if(!registration.is(type))
                continue;

            return (T)(OsUtil.isWindows() ?
                    util.getInstance(registration.getWindows()) :
                    util.getInstance(registration.getLinux()));
        }

        return null;
    }
}
