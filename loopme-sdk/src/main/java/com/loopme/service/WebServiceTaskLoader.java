package com.loopme.service;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by katerina on 5/17/17.
 */

public abstract class WebServiceTaskLoader <T> extends ServiceTaskLoader<T> {

    private static final String LOG_TAG = WebServiceTaskLoader.class.getSimpleName();

    protected WebServiceTaskLoader(final Context context, final Bundle args) {
        super(context, args);
    }

    @Override
    public void deliverResult(final TaskLoaderResult<T> data) {
        super.deliverResult(data);
        if (data.isError() && !isReset() && isStarted()) {
            final Exception error = data.getError();
        }
    }

}
