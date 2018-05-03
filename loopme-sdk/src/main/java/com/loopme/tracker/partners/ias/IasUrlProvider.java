package com.loopme.tracker.partners.ias;

import android.net.Uri;

import com.loopme.BuildConfig;
import com.loopme.Logging;
import com.loopme.tracker.AdIds;

import java.util.HashMap;

import okhttp3.HttpUrl;

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
    private String mLoggingTagUrl;

    private HashMap<String, String> mValuesMap;

    public IasUrlProvider(AdIds adIds) {
        populateData(adIds);
        build();
    }

    private void build() {
        Uri cmTagUri = Uri.parse(BuildConfig.IAS_CM_TAG_URL);
        mCmTagUrl = new HttpUrl.Builder()
                .scheme(cmTagUri.getScheme())
                .host(cmTagUri.getHost())
                .addPathSegment(cmTagUri.getLastPathSegment())
                .addQueryParameter(PARAM_AN_ID, BuildConfig.IAS_LOOPME_PARTNER_ID)
                .addQueryParameter(PARAM_ADV_ID, mValuesMap.get(PARAM_ADV_ID))
                .addQueryParameter(PARAM_CAMP_ID, mValuesMap.get(PARAM_CAMP_ID))
                .addQueryParameter(PARAM_PUB_ID, mValuesMap.get(PARAM_PUB_ID))
                .addQueryParameter(PARAM_CHAN_ID, mValuesMap.get(PARAM_CHAN_ID))
                .addQueryParameter(PARAM_PLACEMENT_ID, mValuesMap.get(PARAM_PLACEMENT_ID))
                .addQueryParameter(PARAM_ADSAFE_PAR, mValuesMap.get(PARAM_ADSAFE_PAR))
                .addQueryParameter(PARAM_BUNDLE_ID, mValuesMap.get(PARAM_BUNDLE_ID))
                .build().toString();

        Uri loggingTagUri = Uri.parse(BuildConfig.IAS_LOGGING_TAG_URL);
        HttpUrl.Builder loggingTagBuilder = new HttpUrl.Builder()
                .scheme(loggingTagUri.getScheme())
                .host(loggingTagUri.getHost());

        for (String segment : loggingTagUri.getPathSegments()) {
            loggingTagBuilder.addPathSegment(segment);
        }
        loggingTagBuilder.addQueryParameter(PARAM_PLACEMENT_ID, mValuesMap.get(PARAM_PLACEMENT_ID));
        mLoggingTagUrl = loggingTagBuilder.build().toString();
    }

    private void populateData(AdIds adIds) {
        mValuesMap = new HashMap<>();
        mValuesMap.put(PARAM_ADV_ID, adIds.getAdvertiserId());
        mValuesMap.put(PARAM_CAMP_ID, adIds.getCampaignId());
        mValuesMap.put(PARAM_PUB_ID, "testPub"); // getPublisherName()
        mValuesMap.put(PARAM_CHAN_ID, adIds.getLineItemId()); // getBundleDomain()
        mValuesMap.put(PARAM_PLACEMENT_ID, adIds.getPlacementId() + "_" + adIds.getAppId()); //need adIds.getAppName() + "_" + adIds.getAppId());
        mValuesMap.put(PARAM_BUNDLE_ID, adIds.getCreativeId()); //getBundleDomain()
        mValuesMap.put(PARAM_ADSAFE_PAR, ADSAFE_PAR);
    }

    public String getCmTagScript() {
        return SCRIPT_START + mCmTagUrl + SCRIPT_END;
    }

    public String getLoggingTagScript() {
        return SCRIPT_START + mLoggingTagUrl + SCRIPT_END;
    }

    public String getCmTagUrl() {
        return mCmTagUrl;
    }

    public String getLoggingTagUrl() {
        return mLoggingTagUrl;
    }
}
