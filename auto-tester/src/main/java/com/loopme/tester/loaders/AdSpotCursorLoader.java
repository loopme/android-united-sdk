package com.loopme.tester.loaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.loopme.tester.model.AdSpot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by katerina on 2/9/17.
 */

public class AdSpotCursorLoader extends CursorLoader {
    private final List<AdSpot> mAdSpotsList = new ArrayList<>();

    public AdSpotCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = super.loadInBackground();
        AdSpot adSpot;
        mAdSpotsList.clear();
        if (cursor != null && !cursor.isClosed() && cursor.getCount() > 0 && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                adSpot = AdSpot.createFromCursor(cursor);
                mAdSpotsList.add(adSpot);
                cursor.moveToNext();
            }
        }
        return cursor;
    }

    public ArrayList<AdSpot> getAdSpotModelList() {
        ArrayList<AdSpot> list = new ArrayList<>();
        list.addAll(mAdSpotsList);
        return list;
    }
}
