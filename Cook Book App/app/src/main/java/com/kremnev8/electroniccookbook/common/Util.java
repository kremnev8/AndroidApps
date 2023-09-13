package com.kremnev8.electroniccookbook.common;

import java.util.concurrent.atomic.AtomicBoolean;

public class Util {

    private static AtomicBoolean isRunningTest;

    public static synchronized boolean isRunningTest () {
        if (null == isRunningTest) {
            boolean istest;

            try {
                // "android.support.test.espresso.Espresso" if you haven't migrated to androidx yet
                Class.forName ("androidx.test.espresso.Espresso");
                istest = true;
            } catch (ClassNotFoundException e) {
                istest = false;
            }

            isRunningTest = new AtomicBoolean (istest);
        }

        return isRunningTest.get();
    }
}
