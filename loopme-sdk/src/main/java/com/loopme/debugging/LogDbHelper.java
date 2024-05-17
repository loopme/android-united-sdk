package com.loopme.debugging;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LogDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LoopMeLogs.db";
    private static final String TABLE_NAME = "loopme_logs";

    public static final int DATABASE_VERSION = 1;

    private static final int MAX_ROW_COUNT = 1000;

    static final String ID = "id";
    static final String LOG = "log";

    private static final String TABLE_CREATE =
        "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement," + LOG + " text not null);";

    public LogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(sqLiteDatabase);
    }

    public void putLog(String logMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (getCount(db) >= MAX_ROW_COUNT) {
            clear();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(LOG, logMessage);
        db.insert(TABLE_NAME, null, values);
    }

    public int getCount(SQLiteDatabase db) {
        String query = "SELECT  * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public List<String> getLogs() {
        List<String> logList = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String logItem = cursor.getString(1);
                logList.add(logItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return logList;
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }
}
