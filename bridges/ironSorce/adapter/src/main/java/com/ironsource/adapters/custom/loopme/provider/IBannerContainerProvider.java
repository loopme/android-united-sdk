package com.ironsource.adapters.custom.loopme.provider;

import com.ironsource.adapters.custom.loopme.LoopmeCustomBanner;
import com.ironsource.mediationsdk.IronSourceBannerLayout;

/**
 * The activity hosting the banner view should implement this interface in order to provide
 * {@link LoopmeCustomBanner} with a reference to the
 * IronSourceBannerLayout view in order to correctly implement mediation.
 */
public interface IBannerContainerProvider {

    IronSourceBannerLayout getBannerContainer();

}
