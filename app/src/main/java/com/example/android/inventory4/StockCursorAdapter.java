package com.example.android.inventory4;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory4.data.StockContract;
import com.squareup.picasso.Picasso;

import static android.R.attr.content;
import static android.R.attr.name;
import static com.example.android.inventory4.R.id.shoppingBasket;
import static com.example.android.inventory4.R.id.supplier;
import static com.example.android.inventory4.data.StockContract.StockEntry;

/**
 * Created by Patty on 10/3/2017.
 */

public class StockCursorAdapter extends CursorAdapter{

    private Object imageString;

    public StockCursorAdapter(Context context, Cursor c) {
        super((Context) context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.book_name);
        TextView supplierTextView = (TextView) view.findViewById(supplier);
        ImageView merchandisePhoto = (ImageView) view.findViewById(R.id.merchandiseImage);
        TextView quantityTextView = (TextView) view.findViewById(R.id.current_inventory_quantity);
        ImageView saleButton = (ImageView) view.findViewById(shoppingBasket);

        ImageView placeOrderEmailImage = (ImageView) view.findViewById(R.id.emailPlaceOrder);

        //find the columns of stock attributes
        int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_NAME);
        int supplierColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_SUPPLIER);
        int imageColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_IMAGE);
        int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_QUANTITY);

        //read stock attributes from the cursor for the current item
        String stockName = cursor.getString(nameColumnIndex);
        String stockSupplier = cursor.getString(supplierColumnIndex);
        merchandisePhoto.setImageURI(imageUri);
        quantityTextView.setText("quantity : " + quantity + "");

        Uri imageUri = null;
        if (imageString == null) {
            Picasso.with(context)
                    .load("")
                    .into(merchandisePhoto);
        }
        else{
            imageUri = Uri.parse(cursor.getString(cursor.getColumnIndex(StockEntry.COLUMN_STOCK_IMAGE)));
    }

    final int newQuantity = quantity;

        placeOrderEmailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEmail(name, quantity, supplier, price, context);
                                          }
                                      });
        if ( quantity > 0 ) {
            saleButton.setOnClickListener(new View.OnClickListener(){
        @Override
                public void onClick(View v){
            ContentValues contentValues = new ContentValues();

            contentValues.put(StockEntry.COLUMN_STOCK_QUANTITY, newQuantity -1);
            Toast.makeText(context, newQuantity + "", Toast.LENGTH_SHORT.show();
            context.getContentResolver().notifyChange(stockUri, null);
            Uri currentMerchandiseUri = ContentUris.withAppendedId
                    (StockEntry.CONTENT_URI, cursor_id);
            context.getContentResolver().update
                    (currentMerchandiseUri, contentValues, null, null);
        }
            });
        }
        else{
            Toast.makeText(context, "Update quantity, cannot be a negative value", Toast.LENGTH_SHORT).show();
        }

        //if stock supplier is empty string or null, then use some default text that
        //says "supplier not available"
        if (TextUtils.isEmpty(stockSupplier)) {
            stockSupplier = "Supplier not available";
        }
        //Update the Textviews with the attributes for the current item
        nameTextView.setText(stockName);
        supplierTextView.setText(stockSupplier);
        }
        //need help with e mail intent please!!!
        /////////


    }


