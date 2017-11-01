package com.example.android.inventory4.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Patty on 10/3/2017.
 */

public final class StockContract {
    private StockContract() {

    }
    public static final String CONTENT_AUTHORITY = "com.example.android.inventory4";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY4 = "inventory";

    public static final class StockEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY4);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY4;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY4;

        public final static String TABLE_NAME = "inventory";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_STOCK_NAME = "name";

        public final static String COLUMN_STOCK_PRICE = "price";

        public final static String COLUMN_STOCK_QUANTITY = "quantity";

        public final static String COLUMN_STOCK_SUPPLIER = "supplier";

        public final static String COLUMN_STOCK_IMAGE = "image";

        public final static String COLUMN_STOCK_SALE = "sale";


    }
}
