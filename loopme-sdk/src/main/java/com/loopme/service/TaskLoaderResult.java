package com.loopme.service;

/**
 * Created by katerina on 5/17/17.
 */

public class TaskLoaderResult<T> {

    private final Exception error;
    private final T data;

    public TaskLoaderResult(T data) {
        this.data = data;
        this.error = null;
    }

    public TaskLoaderResult(Exception error) {
        this.error = error;
        this.data = null;
    }

    public boolean isSuccess() {
        return this.error == null;
    }

    public boolean isError() {
        return this.error != null;
    }

    public Exception getError() {
        return this.error;
    }

    public String getErrorMessage() {
        return this.error != null?this.error.getMessage():null;
    }

    public T getData() {
        return this.data;
    }
}
