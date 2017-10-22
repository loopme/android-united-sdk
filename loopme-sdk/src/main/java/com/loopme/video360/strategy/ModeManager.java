package com.loopme.video360.strategy;

import android.content.Context;

public abstract class ModeManager<T extends IModeStrategy> implements IModeStrategy {
    private T mStrategy;

    /**
     * must call after new instance
     */
    public void prepare(Context context){
        initMode(context);
    }

    abstract public void switchMode(Context context);

    abstract protected T createStrategy();

    private void initMode(Context context){
        if (mStrategy != null) mStrategy.off(context);
        mStrategy = createStrategy();
        mStrategy.on(context);
    }

    @Override
    public void on(Context context) {
        mStrategy.on(context);
    }

    @Override
    public void off(Context context) {
        mStrategy.off(context);
    }

    protected T getStrategy() {
        return mStrategy;
    }
}
