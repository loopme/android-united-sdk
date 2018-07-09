package com.loopme.tracker.partners.ias;

import android.net.Uri;

import com.loopme.BuildConfig;
import com.loopme.tracker.AdIds;

import java.util.HashMap;


public class IasUrlProvider {
    private static final String SCRIPT_START = "<script type=\"text/javascript\" src=\"";
    private static final String SCRIPT_END = "\"></script>\n";

    private static final String PARAM_AN_ID = "anId";
    private static final String PARAM_ADV_ID = "advId";
    private static final String PARAM_CAMP_ID = "campId";
    private static final String PARAM_PUB_ID = "pubId";
    private static final String PARAM_CHAN_ID = "chanId";
    private static final String PARAM_PLACEMENT_ID = "placementId";
    private static final String PARAM_ADSAFE_PAR = "adsafe_par";
    private static final String PARAM_BUNDLE_ID = "bundleId";
    private static final String ADSAFE_PAR = "1"; // is not necessary

    private String mCmTagUrl;

    private HashMap<String, String> mValuesMap;

    public IasUrlProvider(AdIds adIds, String appKey) {
        populateData(adIds, appKey);
        build();
    }

    private void build() {
        Uri cmTagUriSample = Uri.parse(BuildConfig.IAS_CM_TAG_URL);
        mCmTagUrl = new Uri.Builder()
                .scheme(cmTagUriSample.getScheme())
                .authority(cmTagUriSample.getHost())
                .appendPath(cmTagUriSample.getLastPathSegment())
                .appendQueryParameter(PARAM_AN_ID, BuildConfig.IAS_LOOPME_PARTNER_ID)
                .appendQueryParameter(PARAM_ADV_ID, mValuesMap.get(PARAM_ADV_ID))
                .appendQueryParameter(PARAM_CAMP_ID, mValuesMap.get(PARAM_CAMP_ID))
                .appendQueryParameter(PARAM_PUB_ID, mValuesMap.get(PARAM_PUB_ID))
                .appendQueryParameter(PARAM_CHAN_ID, mValuesMap.get(PARAM_CHAN_ID))
                .appendQueryParameter(PARAM_PLACEMENT_ID, mValuesMap.get(PARAM_PLACEMENT_ID))
                .appendQueryParameter(PARAM_ADSAFE_PAR, mValuesMap.get(PARAM_ADSAFE_PAR))
                .appendQueryParameter(PARAM_BUNDLE_ID, mValuesMap.get(PARAM_BUNDLE_ID))
                .build().toString();
    }

    private void populateData(AdIds adIds, String appKey) {
        mValuesMap = new HashMap<>();
        mValuesMap.put(PARAM_ADV_ID, adIds.getAdvertiserId());
        mValuesMap.put(PARAM_CAMP_ID, adIds.getCampaignId());
        mValuesMap.put(PARAM_PUB_ID, adIds.getCompany() + "_" + adIds.getDeveloper());
        mValuesMap.put(PARAM_CHAN_ID, adIds.getBundleDomain());
        mValuesMap.put(PARAM_PLACEMENT_ID, adIds.getAppName() + "_" + appKey);
        mValuesMap.put(PARAM_BUNDLE_ID, adIds.getBundleDomain());
        mValuesMap.put(PARAM_ADSAFE_PAR, ADSAFE_PAR);
    }

    public String getCmTagScript() {
        return SCRIPT_START + mCmTagUrl + SCRIPT_END;
    }

    public String getCmTagUrl() {
        return mCmTagUrl;
    }
}
