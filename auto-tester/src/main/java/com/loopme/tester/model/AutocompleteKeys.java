package com.loopme.tester.model;

import com.loopme.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by katerina on 2/16/17.
 */

public class AutocompleteKeys {
    public static final String LOOPME_BASE_URL = "loopme.me/api/loopme/ads/v3";
    public static final String LOOPME_TEST_URL = "qa02.lmstaging.me/api/loopme/ads/v3";

    public static final String LOOPME_APPKEY_MPU = "test_mpu";
    public static final String LOOPME_APPKEY_INTERSTITIAL_P = "test_interstitial_p";
    public static final String LOOPME_APPKEY_INTERSTITIAL_L = "test_interstitial_l";
    public static final String LOOPME_APPKEY_FIRST = "9e5340067a";
    public static final String LOOPME_APPKEY_SECOND = "f0ce8b4acf";

    public static final String MOPUB_APP_ID_FIRST = "252412d5e9364a05ab77d9396346d73d";
    public static final String MOPUB_APP_ID_SECOND = "24534e1901884e398f1253216226017e";

    public static List<String> getLoopmeBaseUrls() {
        List<String> list = new ArrayList<>();
        list.add(Constants.OPEN_RTB_URL);
        return list;
    }

    public static List<String> getLoopmeAppKeys() {
        List<String> list = new ArrayList<>();
        list.add(AutocompleteKeys.LOOPME_APPKEY_MPU);
        list.add(AutocompleteKeys.LOOPME_APPKEY_INTERSTITIAL_P);
        list.add(AutocompleteKeys.LOOPME_APPKEY_INTERSTITIAL_L);
        list.add(AutocompleteKeys.LOOPME_APPKEY_FIRST);
        list.add(AutocompleteKeys.LOOPME_APPKEY_SECOND);
        return list;
    }

    public static List<String> getMopubAppKeys() {
        List<String> list = new ArrayList<>();
        list.add(AutocompleteKeys.MOPUB_APP_ID_FIRST);
        list.add(AutocompleteKeys.MOPUB_APP_ID_SECOND);
        return list;
    }
}
