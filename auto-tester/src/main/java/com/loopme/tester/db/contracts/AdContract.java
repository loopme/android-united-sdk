package com.loopme.tester.db.contracts;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Katerina Knyrik
 * on 06.12.16.
 */
public class AdContract extends BaseContract {
    public static final String PATH_ADSPOT = "adspot";

    public static class AdEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ADSPOT).build();
        public static final String TABLE_NAME = "adspot";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_APPKEY = "appkey";
        public static final String COLUMN_BASE_URL = "baseurl";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_SDK = "sdk";
        public static final String COLUMN_TIME = "time";

    }
}
