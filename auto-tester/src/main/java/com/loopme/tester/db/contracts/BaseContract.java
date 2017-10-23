package com.loopme.tester.db.contracts;

import android.net.Uri;

/**
 * Created by Katerina Knyrik
 * on 06.12.16.
 */
public class BaseContract {

    public static final String CONTENT_AUTHORITY = "com.loopme" + ".tester";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

}
