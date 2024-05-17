package com.loopme.utils;

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
}
