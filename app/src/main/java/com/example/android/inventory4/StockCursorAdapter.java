package com.example.android.inventory4;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.android.inventory4.R.id.price;
import static com.example.android.inventory4.data.StockContract.StockEntry;

/**
 * Created by Patty on 10/3/2017.
 */

public class StockCursorAdapter extends CursorAdapter {

    private Object imageString;

    public StockCursorAdapter(Context context, Cursor c) {
        super((Context) context, c, 0);
    }

    final int COLUMN_INDEX_ID = 0;
    final int COLUMN_INDEX_NAME = 1;
    final int COLUMN_INDEX_PRICE = 2;
    final int COLUMN_INDEX_QUANTITY = 3;
    final int COLUMN_INDEX_SUPPLIER = 4;
    final int COLUMN_INDEX_IMAGE = 5;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.toy_name);
        TextView supplierTextView = (TextView) view.findViewById(price);
        Button merchandisePhoto = (Button) view.findViewById(R.id.select_image);
        TextView quantityTextView = (TextView) view.findViewById(R.id.current_quantity);
        ImageButton saleButton = (ImageButton) view.findViewById(R.id.green_sale_bag_button);

        ImageButton emailImage = (ImageButton) view.findViewById(R.id.email);

        //find the columns of stock attributes
        int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_PRICE);

        //read stock attributes from the cursor for the current item
        final int stockId = cursor.getInt(COLUMN_INDEX_ID);
        final String stockName = cursor.getString(COLUMN_INDEX_NAME);
        final String stockSupplier = cursor.getString(COLUMN_INDEX_SUPPLIER);
        String supplier = cursor.getString(COLUMN_INDEX_SUPPLIER);
        final String stockPrice = cursor.getString(COLUMN_INDEX_PRICE);
        final int stockQuantity = cursor.getInt(COLUMN_INDEX_QUANTITY);

        Uri imageUri = Uri.parse(cursor.getString(COLUMN_INDEX_IMAGE));

        int quantity = cursor.getInt(quantityColumnIndex);
        quantityTextView.setText("quantity : " + quantity + "");


        emailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEmail(stockName, stockSupplier, stockPrice, stockQuantity, context);
            }
        });

        final int newQuantity = quantity;

        if (quantity > 0) {
            saleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(StockEntry.COLUMN_STOCK_QUANTITY, newQuantity - 1);
                    Toast.makeText(context, newQuantity + "", Toast.LENGTH_SHORT).show();
                    //context.getContentResolver().notifyChange(Uri, null);
                    //context.getContentResolver().update;
                    //or
                    //ContentResolver resolver = view.getConext().getContentResolver();
                    //resolver.update(uri, values, null, null);

                }

            });
        } else {
            Toast.makeText(context, "Update quantity, cannot be a negative value", Toast.LENGTH_SHORT).show();
        }


        //if stock supplier is empty string or null, then use some default text that
        //says "supplier not available"
        if (TextUtils.isEmpty(supplier)) {
            supplier = "Supplier not available";
        }
        //Update the Textviews with the attributes for the current item
        nameTextView.setText(stockName);
        supplierTextView.setText(supplier);}

    // e mail intent - NEED HELP
    ///////////////////////////
    public void createEmail(String toyName, String supplier, String price, int quantity, Context c ) {
        String content = toyName;
        content = content + supplier;
        content = content + price;
        content = content + quantity;
        content = content + imageString;

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("message/rfc822");

        intent.putExtra(Intent.EXTRA_EMAIL, "@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Inventory Order");
        intent.putExtra(Intent.EXTRA_TEXT, "Toy Name, Supplier, Quantity, Price");
        intent.putExtra(Intent.EXTRA_TEXT, content);

    c.startActivity(Intent.createChooser(intent, "Send Email"));


    }
}