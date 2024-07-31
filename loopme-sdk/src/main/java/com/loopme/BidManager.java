package com.loopme;

import com.loopme.models.response.Bid;

public class BidManager {
    private static BidManager instance;
    private Bid currentBid;

    private BidManager() {}

    public static synchronized BidManager getInstance() {
        if (instance == null) {
            instance = new BidManager();
        }
        return instance;
    }

    public void setCurrentBid(Bid bid) {
        this.currentBid = bid;
    }

    public Bid getCurrentBid() {
        return currentBid;
    }

    public String getCurrentCid() {
        return currentBid != null ? currentBid.getCid() : "null";
    }

    public String getCurrentCrid() {
        return currentBid != null ? currentBid.getCrid() : "null";
    }

    public String getRequestId() {
        return currentBid != null ? currentBid.getId() : "null";
    }
}
