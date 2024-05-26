package com.loopme.network.parser;

import android.util.Log;

import com.loopme.Constants;
import com.loopme.models.response.Bid;
import com.loopme.models.response.Ext;
import com.loopme.models.response.ResponseJsonModel;
import com.loopme.models.response.Seatbid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResponseJsonModelParser extends BaseJSONParser {
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
        return equalsToLastPrecessing(jsonObject) ?
            sLastProcessingResult : parseResponseJsonModel(jsonObject);
    }

    private boolean equalsToLastPrecessing(JSONObject jsonObject) {
        return jsonObject != null && jsonObject.toString().equals(sLastProcessingObjectAsString);
    }

    private ResponseJsonModel parseResponseJsonModel(JSONObject jsonObject) throws JSONException {
        saveAsLastProcessingObject(jsonObject);
        String id = getString(jsonObject, PARAM_ID, false);
        List<Seatbid> seatBidsList = parseSeatBidList(jsonObject);
        ResponseJsonModel responseJsonModel = new ResponseJsonModel();
        responseJsonModel.setId(id);
        responseJsonModel.setSeatbid(seatBidsList);
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
        List<Bid> bidList = parseBidList(jsonArray);
        if (bidList.isEmpty()) {
            throw new JSONException("SeatBid list is empty.");
        }
        return new Seatbid(bidList);
    }


    private List<Bid> parseBidList(JSONArray jsonArray) throws JSONException {
        List<Bid> bidList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            bidList.add(parseBid(jsonArray.getJSONObject(i)));
        }
        return bidList;
    }

    private Bid parseBid(JSONObject jsonObject) throws JSONException {
        String adm = getString(jsonObject, PARAM_ADM, true);
        Ext ext;
        if (jsonObject.has(PARAM_EXT)) {
            ext = parseExt(jsonObject.getJSONObject(PARAM_EXT));
        } else {
            ext = getDefaultExt(adm);
        }
        String id = getString(jsonObject, PARAM_ID, false);
        String impid = getString(jsonObject, PARAM_IMP_ID, false);
        String adid = getString(jsonObject, PARAM_AD_ID, false);
        List<String> adomainList = parseStringsList(jsonObject, PARAM_A_DOMAIN);
        String iurl = getString(jsonObject, PARAM_I_URL, false);
        String cid = getString(jsonObject, PARAM_C_ID, false);
        String crid = getString(jsonObject, PARAM_CR_ID, false);
        return new Bid(ext, id, impid, adid, adm, adomainList, iurl, cid, crid);
    }

    private Ext getDefaultExt(String adm) {
        int debug = -1;
        String advertiser = "";
        String lineitem = "";
        String appname = "";
        String crtype = adm.trim().toUpperCase().startsWith("<VAST") ? "VAST" : "MRAID";
        String orientation = "portrait";
        if (crtype.equals("VAST")) {
            orientation = "landscape";
        }
        String campaign = "";
        String developer = "";
        String company = "";
        List<String> measurePartners = new ArrayList<>();
        List<String> package_ids = new ArrayList<>();
        int autoloading = Constants.AUTO_LOADING_ABSENCE;
        return new Ext(advertiser, orientation, debug, lineitem, appname, crtype, campaign, measurePartners, autoloading, package_ids, developer, company);
    }

    private Ext parseExt(JSONObject jsonObject) throws JSONException {
        int debug = getInt(jsonObject, PARAM_EXT_DEBUG, false);
        String advertiser = getString(jsonObject, PARAM_EXT_ADVERTISER, false);
        String orientation = getString(jsonObject, PARAM_EXT_ORIENTATION, false);
        String lineitem = getString(jsonObject, PARAM_EXT_LINE_ITEM, false);
        String appname = getString(jsonObject, PARAM_EXT_APP_NAME, false);
        String crtype = getString(jsonObject, PARAM_EXT_CR_TYPE, true);
        String campaign = getString(jsonObject, PARAM_EXT_CAMPAIGN, false);
        String developer = getString(jsonObject, PARAM_EXT_DEVELOPER, false);
        String company = getString(jsonObject, PARAM_EXT_COMPANY, false);
        List<String> measurePartners = parseStringsList(jsonObject, PARAM_EXT_MEASURE_PARTNERS);
        List<String> package_ids = parseStringsList(jsonObject, PARAM_EXT_PACKAGE_IDS);
        int autoloading = getAutoLoading(jsonObject, Constants.AUTO_LOADING_ABSENCE);
        return new Ext(advertiser, orientation, debug, lineitem, appname, crtype, campaign, measurePartners, autoloading, package_ids, developer, company);
    }

    private int getAutoLoading(JSONObject jsonObject, int defaultValue) {
        try {
            return getInt(jsonObject, PARAM_EXT_AUTOLOADING, true);
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.toString());
        }
        return defaultValue;
    }

    private void saveAsLastProcessingObject(JSONObject jsonObject) {
        sLastProcessingObjectAsString = jsonObject.toString();
    }
}
