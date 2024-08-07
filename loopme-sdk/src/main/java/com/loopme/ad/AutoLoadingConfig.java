package com.loopme.ad;

public abstract class AutoLoadingConfig {
    private boolean sBackendAutoLoadingValue = true;
    private boolean sUserAutoLoadingValue = true;

    public boolean isAutoLoadingEnabled() {
        return sUserAutoLoadingValue && sBackendAutoLoadingValue;
    }

    public void setAutoLoading(boolean autoLoadingEnabled) {
        sUserAutoLoadingValue = autoLoadingEnabled;
    }
    protected void setBackendAutoLoadingValue(boolean autoLoadingEnabled) {
        sBackendAutoLoadingValue = autoLoadingEnabled;
    }
}
