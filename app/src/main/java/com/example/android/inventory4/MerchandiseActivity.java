package com.example.android.inventory4;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import static com.example.android.inventory4.data.StockContract.StockEntry;

/**
 * Created by Patty on 10/3/2017.
 */

public class MerchandiseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int STOCK_LOADER = 0;
    StockCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandise);

        // Setup FAB to open EditorActivity
        android.support.design.widget.FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.exit_button);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MerchandiseActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView stockListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_items);
        stockListView.setEmptyView(emptyView);

        mCursorAdapter = new StockCursorAdapter(this, null);
        stockListView.setAdapter(mCursorAdapter);

        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MerchandiseActivity.this, EditorActivity.class);

                Uri currentStockUri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);
                intent.setData(currentStockUri);
                startActivity(intent);
            }

        });
        getLoaderManager().initLoader(STOCK_LOADER, null, this);
    }

    //Helper method to insert hardcoded stock data into the database. For debugging purposes only.
    private void insertStock() {
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_STOCK_NAME, "Star Trek Action Figure");
        values.put(StockEntry.COLUMN_STOCK_QUANTITY, "20");
        values.put(StockEntry.COLUMN_STOCK_PRICE, 17);
        values.put(StockEntry.COLUMN_STOCK_SUPPLIER, "Target");
        values.put(StockEntry.COLUMN_STOCK_IMAGE, "startrekactionfigure");

        //Uri imageUri = Uri.parse(R.drawable.startrekactionfigure);
        //values.put(COLUMN_STOCK_IMAGE, imageUri.toString());

        Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI,
                values);
    }

    private void deleteAllInventory4() {
        int rowsDeleted = getContentResolver().delete(StockEntry.CONTENT_URI, null, null);
        Log.v("MerchandiseActivity", rowsDeleted + " rows deleted from stock database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_merchandise, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enter_dummy_data:
                insertStock();
                return true;

            case R.id.delete_all_inventory:
                deleteAllInventory4();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_STOCK_NAME,
                StockEntry.COLUMN_STOCK_PRICE,
                StockEntry.COLUMN_STOCK_QUANTITY,
                StockEntry.COLUMN_STOCK_SUPPLIER,
                StockEntry.COLUMN_STOCK_IMAGE};
        //This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                StockEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
