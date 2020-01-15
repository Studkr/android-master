package com.INT.apps.GpsspecialDevelopment.notifications;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Michael Soyma (Created on 10/17/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class NotificationIdGenerator {

    private static NotificationIdGenerator instance;

    public static NotificationIdGenerator getInstance() {
        if (instance == null)
            instance = new NotificationIdGenerator();
        return instance;
    }

    private AtomicInteger id;

    public NotificationIdGenerator() {
        this.id = new AtomicInteger(0);
    }

    public int generateNewId() {
        return id.incrementAndGet();
    }
}
