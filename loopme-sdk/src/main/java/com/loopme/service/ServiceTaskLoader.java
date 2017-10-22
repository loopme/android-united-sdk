package com.loopme.service;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.loopme.ad.LoopMeAd;

/**
 * Created by katerina on 5/17/17.
 */

public abstract class ServiceTaskLoader<T> extends AsyncTaskLoader<TaskLoaderResult<T>> {

    private boolean isLoading = false;
    private boolean isCancelled = false;

    private TaskLoaderResult<T> mData;
    private Bundle args;

    public ServiceTaskLoader(Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    public final TaskLoaderResult<T> loadInBackground() {
        TaskLoaderResult taskLoaderResult;
        try {
            this.isCancelled = false;
            this.isLoading = true;
            Object object = this.onRequest(this.args);
            taskLoaderResult = new TaskLoaderResult(object);
            return taskLoaderResult;
        } catch (Exception ex) {
            taskLoaderResult = new TaskLoaderResult(ex);
        } finally {
            this.isLoading = false;
        }

        return taskLoaderResult;
    }

    public boolean isLoading() {
        return this.isLoading;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public Bundle getArguments() {
        return this.args != null?new Bundle(this.args):null;
    }

    protected abstract LoopMeAd onRequest(Bundle args) throws Exception;

    public void onContentChanged(Bundle args) {
        this.args = args;
        this.onContentChanged();
    }

    public void deliverResult(TaskLoaderResult<T> data) {
        if(this.isReset()) {
            this.releaseResources(data);
        } else {
            TaskLoaderResult<T> oldData = this.mData;
            this.mData = data;
            if(this.isStarted()) {
                super.deliverResult(data);
            }
            if(oldData != null && oldData != data) {
                this.releaseResources(oldData);
            }
        }
    }

    protected void onStartLoading() {
        if(this.mData != null) {
            this.deliverResult(this.mData);
        }

        if(this.takeContentChanged() || this.mData == null) {
            this.forceLoad();
        }
    }

    protected void onStopLoading() {
        this.cancelLoad();
    }

    protected void onReset() {
        this.onStopLoading();
        if(this.mData != null) {
            this.releaseResources(this.mData);
            this.mData = null;
        }
    }

    public void onCanceled(TaskLoaderResult<T> data) {
        this.isCancelled = true;
        super.onCanceled(data);
        this.releaseResources(data);
    }

    protected void releaseResources(TaskLoaderResult<T> data) {
    }
}
