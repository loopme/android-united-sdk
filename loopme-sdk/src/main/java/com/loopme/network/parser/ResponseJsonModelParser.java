package com.loopme.network.parser;

import android.util.Log;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.models.response.Bid;
import com.loopme.models.response.Ext;
import com.loopme.models.response.ResponseJsonModel;
import com.loopme.models.response.Seatbid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResponseJsonModelParser {
    private static final String LOG_TAG = ResponseJsonModelParser.class.getSimpleName();
    private static final String PARAM_ID = "id";
    private static final String PARAM_ADM = "adm";
    private static final String PARAM_BID = "bid";
    private static final String PARAM_EXT = "ext";
    private static final String PARAM_C_ID = "cid";
    private static final String PARAM_CR_ID = "crid";
    private static final String PARAM_AD_ID = "adid";
    private static final String PARAM_I_URL = "iurl";
    private static final String PARAM_IMP_ID = "impid";
    private static final String PARAM_SEAT_BID = "seatbid";
    private static final String PARAM_A_DOMAIN = "adomain";

    private static final String PARAM_EXT_DEBUG = "debug";
    private static final String PARAM_EXT_AUTOLOADING = "autoloading";
    private static final String PARAM_EXT_COMPANY = "company";
    private static final String PARAM_EXT_DEVELOPER = "developer";
    private static final String PARAM_EXT_ADVERTISER = "advertiser";
    private static final String PARAM_EXT_ORIENTATION = "orientation";
    private static final String PARAM_EXT_LINE_ITEM = "lineitem";
    private static final String PARAM_EXT_APP_NAME = "appname";
    private static final String PARAM_EXT_CR_TYPE = "crtype";
    private static final String PARAM_EXT_CAMPAIGN = "campaign";
    private static final String PARAM_EXT_PACKAGE_IDS = "package_ids";
    private static final String PARAM_EXT_MEASURE_PARTNERS = "measure_partners";

    private static String sLastProcessingObjectAsString;
    private static ResponseJsonModel sLastProcessingResult;

    public ResponseJsonModel parse(JSONObject jsonObject) throws JSONException {
        boolean isEqualToLastProcessing = jsonObject != null && jsonObject.toString().equals(sLastProcessingObjectAsString);
        return isEqualToLastProcessing ? sLastProcessingResult : parseResponseJsonModel(jsonObject);
    }

    private ResponseJsonModel parseResponseJsonModel(JSONObject jsonObject) throws JSONException {
        sLastProcessingObjectAsString = jsonObject.toString();
        ResponseJsonModel responseJsonModel = new ResponseJsonModel();
        responseJsonModel.setId(getString(jsonObject, PARAM_ID, false));
        responseJsonModel.setSeatbid(parseSeatBidList(jsonObject));
        sLastProcessingResult = responseJsonModel;
        return responseJsonModel;
    }

    private List<Seatbid> parseSeatBidList(JSONObject jsonObject) throws JSONException {
        JSONArray seatBidArray = jsonObject.getJSONArray(PARAM_SEAT_BID);
        List<Seatbid> seatBidList = new ArrayList<>();
        for (int i = 0; i < seatBidArray.length(); i++) {
            JSONObject seatBidAsJson = seatBidArray.getJSONObject(i);
            seatBidList.add(parseSeatBid(seatBidAsJson));
        }
        return seatBidList;
    }

    private Seatbid parseSeatBid(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = jsonObject.getJSONArray(PARAM_BID);
        List<Bid> bidList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            bidList.add(parseBid(jsonArray.getJSONObject(i)));
        }
        if (bidList.isEmpty()) {
            throw new JSONException("SeatBid list is empty.");
        }
        return new Seatbid(bidList);
    }

    private Bid parseBid(JSONObject jsonObject) throws JSONException {
        String adm = getString(jsonObject, PARAM_ADM, true);
        return new Bid(
            jsonObject.has(PARAM_EXT) ?
                parseExt(jsonObject.getJSONObject(PARAM_EXT)) : getDefaultExt(adm),
            getString(jsonObject, PARAM_ID, false),
            getString(jsonObject, PARAM_IMP_ID, false),
            getString(jsonObject, PARAM_AD_ID, false),
            adm,
            parseStringsList(jsonObject, PARAM_A_DOMAIN),
            getString(jsonObject, PARAM_I_URL, false),
            getString(jsonObject, PARAM_C_ID, false),
            getString(jsonObject, PARAM_CR_ID, false)
        );
    }

    private Ext getDefaultExt(String adm) {
        boolean isVast = adm.trim().toUpperCase().startsWith("<VAST");
        return new Ext(
            "",
            isVast ? "landscape" : "portrait",
            -1,
            "",
            "",
            isVast ? "VAST" : "MRAID",
            "",
            new ArrayList<>(),
            Constants.AUTO_LOADING_ABSENCE,
            new ArrayList<>(),
            "",
            ""
        );
    }

    private Ext parseExt(JSONObject jsonObject) throws JSONException {
        int autoLoading;
        try {
            autoLoading = getInt(jsonObject, PARAM_EXT_AUTOLOADING, true);
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.toString());
            autoLoading = Constants.AUTO_LOADING_ABSENCE;
        }

        return new Ext(
            getString(jsonObject, PARAM_EXT_ADVERTISER, false),
            getString(jsonObject, PARAM_EXT_ORIENTATION, false),
            getInt(jsonObject, PARAM_EXT_DEBUG, false),
            getString(jsonObject, PARAM_EXT_LINE_ITEM, false),
            getString(jsonObject, PARAM_EXT_APP_NAME, false),
            getString(jsonObject, PARAM_EXT_CR_TYPE, true),
            getString(jsonObject, PARAM_EXT_CAMPAIGN, false),
            parseStringsList(jsonObject, PARAM_EXT_MEASURE_PARTNERS),
            autoLoading,
            parseStringsList(jsonObject, PARAM_EXT_PACKAGE_IDS),
            getString(jsonObject, PARAM_EXT_DEVELOPER, false),
            getString(jsonObject, PARAM_EXT_COMPANY, false)
        );
    }

    private int getInt(JSONObject jsonObject, String param, boolean throwException) throws JSONException {
        try {
            return jsonObject.getInt(param);
        } catch (JSONException e) {
            Logging.out(LOG_TAG, "Parameter " + param + " is absent. Obj: " + jsonObject);
            if (throwException) {
                throw e;
            }
        }
        return 0;
    }

    private String getString(JSONObject jsonObject, String param, boolean throwException) throws JSONException {
        try {
            return jsonObject.getString(param);
        } catch (JSONException e) {
            Logging.out(LOG_TAG, "Parameter " + param + " is absent. Obj: " + jsonObject);
            if (throwException) {
                throw e;
            }
        }
        return "";
    }

    private List<String> parseStringsList(JSONObject jsonObject, String param) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(param);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
            return list;
        } catch (JSONException e) {
            Logging.out(LOG_TAG, "Parameter " + param + " is absent. Obj: " + jsonObject);
            return new ArrayList<>();
        }
    }
}
