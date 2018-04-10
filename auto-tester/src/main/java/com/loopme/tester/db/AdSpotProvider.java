package com.loopme.tester.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.loopme.tester.db.contracts.AdContract;
import com.loopme.tester.db.contracts.BaseContract;

import java.util.Arrays;

import static com.loopme.tester.db.contracts.AdContract.AdEntry.TABLE_NAME;
import static com.loopme.tester.db.contracts.AdContract.PATH_ADSPOT;

/**
 * Created by katerina on 2/9/17.
 */

public class AdSpotProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private AdSpotDatabase mOpenHelper;

    private static final int ADSPOT = 100;
    private static final int ADSPOT_ID = 101;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(BaseContract.CONTENT_AUTHORITY, PATH_ADSPOT, ADSPOT);
        matcher.addURI(BaseContract.CONTENT_AUTHORITY, PATH_ADSPOT + "/#", ADSPOT_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new AdSpotDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case ADSPOT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case ADSPOT_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_NAME,
                        projection,
                        addSelection(selection, AdContract.AdEntry.COLUMN_ID + "=?"),
                        new String[]{
                                String.valueOf(ContentUris.parseId(uri))
                        },
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private static String addSelection(String selection, String selectionToAppend) {
        if (TextUtils.isEmpty(selectionToAppend)) {
            return selection;
        }
        String newSelection;
        if (selection != null) {
            newSelection = selection + " AND ";
        } else {
            newSelection = "";
        }
        return newSelection + selectionToAppend;
    }

    private static String[] addSelectionArg(String[] selectionArgs, String args[]) {
        if (args == null) {
            return selectionArgs;
        }
        String[] newSelectionArgs;
        if (selectionArgs != null) {
            int newLength = selectionArgs.length + args.length;
            newSelectionArgs = Arrays.copyOf(selectionArgs, newLength);
            System.arraycopy(args, 0, newSelectionArgs, selectionArgs.length, args.length);
        } else {
            newSelectionArgs = args;
        }
        return newSelectionArgs;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;
        switch (match) {
            case ADSPOT: {
                long _id = db.insertOrThrow(
                        TABLE_NAME,
                        null,
                        values);
                if (_id > 0) {
                    returnUri = AdContract.AdEntry.buildUri(_id);
                }
                System.out.println("Failed to insert row into " + uri + " id: " + _id);
//                else {
//                    throw new android.database.SQLException("Failed to insert row into " + uri + " id: " + _id + " values: "+ );
//                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case ADSPOT:
                rowsDeleted = db.delete(
                        TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case ADSPOT_ID:
                rowsDeleted = db.delete(
                        TABLE_NAME,
                        AdContract.AdEntry.COLUMN_ID + "=?",
                        new String[]{
                                String.valueOf(ContentUris.parseId(uri))
                        });
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case ADSPOT:
                rowsUpdated = db.update(
                        TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case ADSPOT_ID:
                rowsUpdated = db.update(
                        TABLE_NAME,
                        values,
                        AdContract.AdEntry.COLUMN_ID + "=?",
                        new String[]{
                                String.valueOf(ContentUris.parseId(uri))
                        });
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return 0;
    }

}
