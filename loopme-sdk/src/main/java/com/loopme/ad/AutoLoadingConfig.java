package com.loopme.ad;

import com.loopme.utils.Utils;

public abstract class AutoLoadingConfig {
    private boolean sBackendAutoLoadingValue = true;
    private boolean sUserAutoLoadingValue = true;

    public boolean isAutoLoadingEnabled() {
        return sUserAutoLoadingValue && sBackendAutoLoadingValue && !Utils.isApi19();
    }

    public void setAutoLoading(boolean autoLoadingEnabled) {
        sUserAutoLoadingValue = autoLoadingEnabled;
    }

    protected void setBackendAutoLoadingValue(boolean autoLoadingEnabled) {
        sBackendAutoLoadingValue = autoLoadingEnabled;
    }
}
