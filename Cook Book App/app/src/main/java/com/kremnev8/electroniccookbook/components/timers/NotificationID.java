package com.kremnev8.electroniccookbook.components.timers;

import java.util.concurrent.atomic.AtomicInteger;

public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(2);
    public static int getID() {
        return c.incrementAndGet();
    }
}
