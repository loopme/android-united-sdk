package com.loopme.utils;

import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorHelper {

    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();
    private static final ExecutorService sSingleExecutor = Executors.newSingleThreadExecutor();

    private ExecutorHelper() { }

    public static ExecutorService getExecutor() {
        return sExecutor;
    }

    public static ExecutorService getSingleExecutor() {
        return sSingleExecutor;
    }

    public static void executeOnWorkerThread(Runnable task) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            sExecutor.submit(task);
        } else {
            task.run();
        }
    }
}
