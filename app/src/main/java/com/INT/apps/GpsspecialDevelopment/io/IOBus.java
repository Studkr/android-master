package com.INT.apps.GpsspecialDevelopment.io;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import timber.log.Timber;

/**
 * Created by shrey on 23/4/15.
 */
public class IOBus extends Bus {
    private static IOBus sIOBus;

    private IOBus(ThreadEnforcer enforcer) {
        super(enforcer);
    }

    final synchronized static public IOBus getInstance() {
        if (sIOBus == null) {
            sIOBus = new IOBus(ThreadEnforcer.ANY);
        }
        return sIOBus;
    }

    @Override
    public void unregister(Object object) {
        try {
            super.unregister(object);
        } catch (IllegalArgumentException e) {
            Timber.tag("IllegalArgument").e(e.getMessage());
        }
    }
}
