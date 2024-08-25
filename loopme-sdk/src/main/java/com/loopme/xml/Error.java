package com.loopme.xml;

import androidx.annotation.NonNull;

import com.loopme.parser.xml.Text;

public class Error {

    @Text
    private String text;

    @NonNull
    public String getText() { return text == null ? "" : text; }
}
