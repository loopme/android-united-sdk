package com.loopme.tester.qr.model;

import android.text.TextUtils;
import android.webkit.URLUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class AdDescriptorUtils {
    public static boolean isValid(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            AdDescriptor descriptor = mapper.readValue(content, AdDescriptor.class);
            if (URLUtil.isNetworkUrl(descriptor.getUrl())) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static AdDescriptor parseAdDescriptor(String content) {
        AdDescriptor descriptor = new AdDescriptor();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, AdDescriptor.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return descriptor;
    }
}
