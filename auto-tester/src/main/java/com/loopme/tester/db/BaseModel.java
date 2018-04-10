package com.loopme.tester.db;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Katerina Knyrik
 * on 06.12.16.
 */
public abstract class BaseModel {

    @SuppressWarnings("unused")
    public abstract ContentValues toContentValues();

    @SuppressWarnings("unused")
    public abstract void fromCursor(Cursor cursor);

    public String getString(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndexOrThrow(column));
    }

    public long getLong(Cursor cursor, String column) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(column));
    }

    public double getDouble(Cursor cursor, String column) {
        return cursor.getDouble(cursor.getColumnIndexOrThrow(column));
    }

    public int getInt(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(column));
    }

    public boolean getBoolean(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(column)) != 0;
    }
}
