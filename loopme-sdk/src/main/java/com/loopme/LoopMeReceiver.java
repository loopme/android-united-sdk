package com.loopme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class LoopMeReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "data";

    @Override
    public void onReceive(final Context context, Intent intent) {
//        Logging.out(LOG_TAG, "onReceive");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        final SharedPreferences sp = context.getSharedPreferences(Constants.LOOPME_PREFERENCES,
                Context.MODE_PRIVATE);
        String oldId = sp.getString(Constants.VIEWER_TOKEN, "");

//        DataCollector.getInstance(context).onReceive(oldId);
    }
}
