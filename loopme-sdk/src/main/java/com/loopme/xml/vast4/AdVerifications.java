package com.loopme.xml.vast4;

import com.loopme.parser.xml.Tag;

import java.util.List;

public class AdVerifications {

    @Tag("Verification")
    private List<Verification> verificationList;

    public List<Verification> getVerificationList() { return verificationList; }
}
