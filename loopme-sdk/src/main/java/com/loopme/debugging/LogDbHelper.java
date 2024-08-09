package com.loopme.debugging;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LogDbHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "loopme_logs";
    static final String LOG = "log";

    private static final String TABLE_CREATE =
        "create table " + TABLE_NAME + " (id integer primary key autoincrement, log text not null);";

    public LogDbHelper(Context context) {
        super(context, "LoopMeLogs.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) { sqLiteDatabase.execSQL(TABLE_CREATE); }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void putLog(String logMessage) {
        SQLiteDatabase db = getWritableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
            if (cursor.getCount() >= 1000) {
                clear();
                return;
            }
        }
        ContentValues values = new ContentValues();
        values.put(LOG, logMessage);
        db.insert(TABLE_NAME, null, values);
    }

    public List<String> getLogs() {
        try (Cursor cursor = getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null)) {
            List<String> logs = new ArrayList<>();
            while (cursor.moveToNext()) logs.add(cursor.getString(1));
            return logs;
        }
    }

    public void clear() { getWritableDatabase().delete(TABLE_NAME, null, null); }
}
