package com.loopme.xml.vast4;

import com.loopme.parser.xml.Attribute;
import com.loopme.parser.xml.Text;

public class Pricing {

    @Attribute
    private String model;

    @Attribute
    private String currency;

    @Text
    private String amount;

    public String getAmount() {
        return amount;
    }

    public String getModel() {
        return model;
    }

    public String getCurrency() {
        return currency;
    }

}
