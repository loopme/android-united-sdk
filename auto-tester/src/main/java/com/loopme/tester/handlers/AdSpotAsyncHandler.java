package com.loopme.tester.handlers;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.loopme.tester.db.contracts.AdContract;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.ui.fragment.screen.AdSpotCardFragment;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by katerina on 2/12/17.
 */

public class AdSpotAsyncHandler extends AsyncQueryHandler {

    private static final int TOKEN_INSERT_OR_REPLACE = 1;
    private static final int TOKEN_DELETE = 2;
    private static final int TOKEN_SEARCH_ADSPOT = 3;
    private AdSpotCardFragment.OnAdSpotUpdateCallback mOnAdSpotUpdateCallback;


    public AdSpotAsyncHandler(ContentResolver contentResolver) {
        super(contentResolver);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);

        switch (token) {
            case TOKEN_INSERT_OR_REPLACE: {
                if (cookie != null && cookie instanceof AdSpot) {
                    AdSpot adSpot = (AdSpot) cookie;
                    onQueryCompleteForAdSpot(cursor, adSpot);
                }
                break;
            }
            case TOKEN_SEARCH_ADSPOT: {
                if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
                    AdSpot adSpot = AdSpot.createFromCursor(cursor);
                    onAdSpotUpdate(adSpot);
                }
                break;
            }
        }
    }

    private void onAdSpotUpdate(AdSpot adSpot) {
        if (mOnAdSpotUpdateCallback != null) {
            mOnAdSpotUpdateCallback.onAdSpotUpdate(adSpot);
        }
    }

    public void insertOrUpdateAdSpot(AdSpot adSpot) {
        add(adSpot, TOKEN_INSERT_OR_REPLACE);
    }

    private static long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public void insertAsync(AdSpot adSpot) {
        adSpot.setTime(getCurrentTime());
        startInsert(0, null, AdContract.AdEntry.CONTENT_URI, adSpot.toContentValues());
    }

    public void updateAsync(long id, AdSpot adSpot) {
        adSpot.setTime(Calendar.getInstance().getTimeInMillis());
        startUpdate(0, adSpot, AdContract.AdEntry.buildUri(id), adSpot.toContentValues(), null, null);
    }

    public void deleteAsync(long id, AdSpot adSpot) {
        startDelete(0, adSpot, AdContract.AdEntry.buildUri(id), null, null);
    }

    public void deleteAllAsync() {
        startDelete(0, null, AdContract.AdEntry.CONTENT_URI, null, null);
    }

    private void add(final AdSpot adSpot, int token) {
        startQuery(
                token,
                adSpot,
                AdContract.AdEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    private void updateAsync(Uri uri, ContentValues contentValues) {
        startUpdate(0, null, uri, contentValues, null, null);
    }

    private void onQueryCompleteForAdSpot(Cursor cursor, AdSpot adSpot) {
        if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
            final long id = cursor.getLong(cursor.getColumnIndexOrThrow(AdContract.AdEntry.COLUMN_ID));
            adSpot.setTime(getCurrentTime());
            updateAsync(id, adSpot);
        } else {
            insertAsync(adSpot);
        }
    }

    private void insertOrUpdate(Cursor cursor, AdSpot adSpot, int token) {
        if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
            final long id = cursor.getLong(cursor.getColumnIndexOrThrow(AdContract.AdEntry.COLUMN_ID));
            AdSpot existModel = new AdSpot();
            existModel.fromCursor(cursor);
            existModel.setTime(getCurrentTime());
            updateAsync(id, existModel);
        } else {
            insertAsync(adSpot);
        }

    }

    public void insertAllAsync(ArrayList<AdSpot> adSpotsList) {
        for (AdSpot adSpot : adSpotsList) {
            insertAsync(adSpot);
        }
    }

    public void findAdSpotById(long adSpotId, AdSpotCardFragment.OnAdSpotUpdateCallback callback) {
        mOnAdSpotUpdateCallback = callback;
        startQuery(TOKEN_SEARCH_ADSPOT,
                null,
                AdContract.AdEntry.CONTENT_URI,
                null,
                "( " + AdContract.AdEntry.COLUMN_ID + " like ? )",
                new String[]{"%" + adSpotId + "%"},
                null);
    }
}
