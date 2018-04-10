package com.loopme.tester.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.loopme.tester.db.contracts.AdContract.AdEntry;

import java.util.Calendar;

/**
 * Created by katerina on 2/9/17.
 */

public class AdSpotDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "adspot.db";
    private static final int DATABASE_VERSION = 1;

    public AdSpotDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAdSpotTable(db);
    }

    private void createAdSpotTable(SQLiteDatabase db) {
        String SQL_CREATE = "CREATE TABLE " + AdEntry.TABLE_NAME +
                " (" + AdEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AdEntry.COLUMN_NAME + " VARCHAR(64) UNIQUE NOT NULL," +
//                AdEntry.COLUMN_APPKEY + " VARCHAR(64) NOT NULL, " +
                AdEntry.COLUMN_APPKEY + " VARCHAR(64), " +
                AdEntry.COLUMN_BASE_URL + " VARCHAR(64), " +
                AdEntry.COLUMN_TYPE + " VARCHAR(64) NOT NULL, " +
                AdEntry.COLUMN_SDK + " VARCHAR(64) NOT NULL, " +
                AdEntry.COLUMN_TIME + " INTEGER default " + Calendar.getInstance().getTimeInMillis() + ", " +
                "UNIQUE (" + AdEntry.COLUMN_NAME + ") ON CONFLICT IGNORE);";

        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AdEntry.TABLE_NAME);
        onCreate(db);
    }
}
