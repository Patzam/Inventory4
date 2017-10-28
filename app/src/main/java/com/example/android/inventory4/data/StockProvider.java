package com.example.android.inventory4.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.inventory4.data.StockContract.CONTENT_AUTHORITY;
import static com.example.android.inventory4.data.StockDbHelper.LOG_TAG;

/**
 * Created by Patty on 10/3/2017.
 */

public class StockProvider extends ContentProvider {

    private static final int INVENTORY4 = 100;

    private static final int STOCK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, StockContract.PATH_INVENTORY4, INVENTORY4);

        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_INVENTORY4 + "/#", STOCK_ID);

    }

    private StockDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new StockDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case INVENTORY4:
                cursor = database.query(StockContract.StockEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case STOCK_ID:
                selection = StockContract.StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StockContract.StockEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI cannot be queried" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY4:
                return insertStock(uri, contentValues);
            default:
                throw new IllegalArgumentException("Data is not supported for " + uri);
        }
    }
    private Uri insertStock(Uri uri, ContentValues values) {

        String name = values.getAsString(StockContract.StockEntry.COLUMN_STOCK_NAME);
        if (name == null || name.equals("") || name.equals(" ")) {
            throw new IllegalArgumentException("Stock must have a name");
        }
        Integer price = values.getAsInteger(StockContract.StockEntry.COLUMN_STOCK_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Stock must have a price");
        }

        Integer quantity = values.getAsInteger(StockContract.StockEntry.COLUMN_STOCK_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Stock must have proper quantity");
        }

        String image = values.getAsString(StockContract.StockEntry.COLUMN_STOCK_IMAGE);
        if (image == null || image.equals("") || name.equals(" ")) {
            throw new IllegalArgumentException("Must provide Stock image");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(StockContract.StockEntry.TABLE_NAME, null, values);
        if ( id == -1) {
            Log.e(LOG_TAG, " insert to database failed " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY4:
                return updateStock(uri, contentValues, selection, selectionArgs);
            case STOCK_ID:
                selection = StockContract.StockEntry._ID + "=?";

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateStock(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update not valid for " + uri);
        }
    }
    private int updateStock(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(StockContract.StockEntry.COLUMN_STOCK_NAME)) {
            String name = values.getAsString(StockContract.StockEntry.COLUMN_STOCK_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Stock must have a name");
            }
        }
        if (values.containsKey(StockContract.StockEntry.COLUMN_STOCK_PRICE)) {
            Integer price = values.getAsInteger(StockContract.StockEntry.COLUMN_STOCK_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Stock must have a price");
            }

        }
        if (values.containsKey(StockContract.StockEntry.COLUMN_STOCK_QUANTITY)) {
            Integer quantity = values.getAsInteger(StockContract.StockEntry.COLUMN_STOCK_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Stock must have proper quantity");
            }

        }
        if (values.containsKey(StockContract.StockEntry.COLUMN_STOCK_IMAGE)) {
            String image = values.getAsString(StockContract.StockEntry.COLUMN_STOCK_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException("Must provide Stock image");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(StockContract.StockEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY4:
                rowsDeleted = database.delete(StockContract.StockEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case STOCK_ID:
                selection = StockContract.StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(StockContract.StockEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion not valid for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
