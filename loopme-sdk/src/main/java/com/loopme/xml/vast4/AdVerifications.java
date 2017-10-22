package com.loopme.xml.vast4;

import com.loopme.parser.xml.Tag;

import java.util.ArrayList;
import java.util.List;

public class AdVerifications {

    @Tag("Verification")
    private List<Verification> verificationList;

    public List<Verification> getVerificationList() {
        return verificationList;
    }

    public List<String> getJavaScriptResourceList() {
        if (verificationList != null) {
            List<String> javaScriptResourceList = new ArrayList<>();
            for (Verification verification : verificationList) {
                if (verification.getJavaScriptResource() != null) {
                    javaScriptResourceList.add(verification.getJavaScriptResource().getText());
                }
            }
            return javaScriptResourceList;
        }
        return new ArrayList<>();
    }
}
