package com.ironsource.adapters.custom.loopme.exception;

import androidx.annotation.Nullable;

import com.ironsource.adapters.custom.loopme.provider.IBannerContainerProvider;

public class BannerContainerProviderNotImplementedException extends NoSuchMethodException {

    private String className;

    public BannerContainerProviderNotImplementedException(String s) {
        super(s);
        className = s;
    }

    @Nullable
    @Override
    public String getMessage() {
        return className +
                " does not implement <getBannerContainer()> from " +
                IBannerContainerProvider.class.getName();
    }
}
