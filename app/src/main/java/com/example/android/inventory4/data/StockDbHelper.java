package com.example.android.inventory4.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.inventory4.data.StockContract.*;

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
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + StockEntry.TABLE_NAME + " ("
                + StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StockEntry.COLUMN_STOCK_NAME + " TEXT NOT NULL, "
                + StockEntry.COLUMN_STOCK_PRICE + " INTEGER NOT NULL,"
                + StockEntry.COLUMN_STOCK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + StockEntry.COLUMN_STOCK_SUPPLIER + " TEXT, "
                + StockEntry.COLUMN_STOCK_IMAGE + " TEXT );";

        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StockEntry.TABLE_NAME);
        onCreate(db);

    }
}
