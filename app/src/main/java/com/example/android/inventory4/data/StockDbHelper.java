package com.example.android.inventory4.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Patty on 10/3/2017.
 */

public class StockDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = StockDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "stock.db";

    private static final int DATABASE_VERSION = 1;

    public StockDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + StockContract.StockEntry.TABLE_NAME + " ("
                + StockContract.StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StockContract.StockEntry.COLUMN_STOCK_NAME + " TEXT NOT NULL, "
                + StockContract.StockEntry.COLUMN_STOCK_PRICE + " INTEGER NOT NULL,"
                + StockContract.StockEntry.COLUMN_STOCK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + StockContract.StockEntry.COLUMN_STOCK_SUPPLIER + " TEXT, "
                + StockContract.StockEntry.COLUMN_STOCK_IMAGE + " TEXT );";

        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StockContract.StockEntry.TABLE_NAME);
        onCreate(db);

    }
}
