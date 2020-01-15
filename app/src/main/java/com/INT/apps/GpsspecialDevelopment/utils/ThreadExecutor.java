package com.INT.apps.GpsspecialDevelopment.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

public class ThreadExecutor implements Executor {

    public void execute(@NonNull Runnable r) {
        new Thread(r).start();
    }
}
