package com.example.android.inventory4;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventory4.data.StockContract.StockEntry;
import com.squareup.picasso.Picasso;

import static com.example.android.inventory4.data.StockDbHelper.LOG_TAG;

/**
 * Created by Patty on 10/3/2017.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    int quantity = 1;

    private Object imageString;

    private ImageView imageView;

    Uri imageUri;

    private static final int EXISTING_STOCK_LOADER = 0;

    private Uri mCurrentStockUri;

    private EditText mNameEditText;

    private EditText mSupplierEditText;

    private EditText mPriceEditText;

    private TextView mQuantityTextView;

    private ImageView mImageEditText;

    private Button plusButton, minusButton, selectImage;

    private boolean mStockHasChanged = false;

    private static final int IMAGE_INTENT_REQUEST = 0;

    private Uri mImageUri;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mStockHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        final Intent intent = getIntent();
        mCurrentStockUri = intent.getData();
        //This is new stock, so change the app bar to say "Add Stock"
        if (mCurrentStockUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_stock));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete stock that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            //Otherwise this is an existing stock, so change app bar to say "Edit an Item"
            setTitle(getString(R.string.editor_activity_title_edit_stock));
            // Initialize a loader to read the stock data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_STOCK_LOADER, null, this);
        }

        imageView = (ImageView) findViewById(R.id.drop_image);
        selectImage = (Button) findViewById(R.id.select_image);
        imageView.setImageURI(null);


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_INTENT_REQUEST);

            }
        });

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        mSupplierEditText = (EditText) findViewById(R.id.edit_item_supplier);
        mPriceEditText = (EditText) findViewById(R.id.edit_item_price);
        mQuantityTextView = (TextView) findViewById(R.id.quantity);

        //Quantity button plus and minus
        plusButton = (Button) findViewById(R.id.plus_button);
        minusButton = (Button) findViewById(R.id.minus_button);

        plusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                if (TextUtils.isEmpty(mQuantityTextView.getText().toString()))
                {
                    Toast.makeText(EditorActivity.this, " quantity required ", Toast.LENGTH_SHORT).show();
                    try {
                    } catch (Exception e) {
                        Log.e(LOG_TAG, " Must contain quantity. ", e);
                    }
                }
                else {
                    increaseNumber();
                }
            }

        });
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mQuantityTextView.getText().toString()))
                {
                    Toast.makeText(EditorActivity.this, " quantity required ", Toast.LENGTH_SHORT).show();
                    try {
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Must contain quantity.", e);
                    }
                }
                else{
                    decreaseNumber();
                }

            }

        });

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityTextView.setOnTouchListener(mTouchListener);
        //mImageEditText.setOnTouchListener(mTouchListener);

    }

    public void increaseNumber() {
        quantity = quantity + 1;
       display(quantity);
    }

    public void decreaseNumber() {
        if (quantity == 1) {
          Toast.makeText(this, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
        return;
        }
        quantity = quantity - 1;
        display(quantity);
    }


    // Get user input from editor and save stock into database
    private void saveStock() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();


        if (mImageUri == null) {
            Toast.makeText(this, " Must select image ", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageString = mImageUri.toString().trim();


        // Check if this is supposed to be a new stock
        // and check if all the fields in the editor are blank
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, " Must add name ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(supplierString)){
            Toast.makeText(this, " Must add supplier ", Toast.LENGTH_SHORT).show();
        return;
    }
        if (TextUtils.isEmpty(priceString)){
            Toast.makeText(this, " Must add price ", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityString);

        // Create a ContentValues object where column names are the keys,
        // and stock attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_STOCK_NAME, nameString);
        values.put(StockEntry.COLUMN_STOCK_SUPPLIER, supplierString);
        values.put(StockEntry.COLUMN_STOCK_PRICE, priceString);
        values.put(StockEntry.COLUMN_STOCK_QUANTITY, quantityString);
        values.put(StockEntry.COLUMN_STOCK_IMAGE, imageString);

        // Determine if this is new or existing stock by checking if mCurrentStockUri is null or not
        if (mCurrentStockUri == null) {
            // This is a NEW stock item, so insert a new stock item into the provider,
            // returning the content URI for the new stock.
            Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_stock_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_stock_insert_successful),
                        Toast.LENGTH_SHORT).show();
            }

        } else {
            int rowsAffected = getContentResolver().update(mCurrentStockUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_stock_update_failed),
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(this, getString(R.string.editor_stock_update_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentStockUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_item_from_menu);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_item_from_menu:
                saveStock();
                //finish();
                return true;


            case R.id.delete_item_from_menu:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mStockHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);

                            }

                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_INTENT_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mImageUri = data.getData();
                imageView.setImageURI(mImageUri);
            }

        }


    }


    @Override
    public void onBackPressed() {
        //If stock has not changed, continue w handling back button press
        if (!mStockHasChanged) {
            super.onBackPressed();
            return;
        }


        //If there are unsaved changes, setup a dialog to warn the user.
        //Create click listerner to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //close the current activity
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        if ((mCurrentStockUri == null)) {
            return null;
        }
        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_STOCK_NAME,
                StockEntry.COLUMN_STOCK_SUPPLIER,
                StockEntry.COLUMN_STOCK_PRICE,
                StockEntry.COLUMN_STOCK_QUANTITY,
                StockEntry.COLUMN_STOCK_IMAGE};

        return new CursorLoader(this,
                mCurrentStockUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_NAME);
            int supplierColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_SUPPLIER);
            int priceColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String imageString = cursor.getString(imageColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mSupplierEditText.setText(supplier);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityTextView.setText(Integer.toString(quantity));

            if (imageString == null) {
                Picasso.with(getApplicationContext())
                        .load("")
                        .into(imageView);
            } else {
                mImageUri = Uri.parse(cursor.getString(cursor.getColumnIndex(StockEntry.COLUMN_STOCK_IMAGE)));
            }
            imageView.setImageURI(mImageUri);

        }

    }

    public void onLoaderReset(Loader<Cursor> loader) {
        //mNameEditText.setText("");
        //mSupplierEditText.setText("");
        //mPriceEditText.setText("");
        //mQuantityTextView.setText("");
        //mImageEditText.setImageResource(Integer.parseInt(""));
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_for_unsaved_messages);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the stock item.
                if (dialog != null) {
                    dialog.dismiss();

                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    //Prompt the user to confirm that they want to delete this toy.
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_stock_confirmation_question);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the stock.
                deleteStock();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Perform the deletion of the stock in the database.
    private void deleteStock() {
        // Only perform the delete if this is an existing stock
        if (mCurrentStockUri != null) {
            // Call the ContentResolver to delete the stock at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentStockUri
            // content URI already identifies the stock that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentStockUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_stock_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_stock_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public String createEmail(String toyName, String supplier, String price, int quantity) {
        String emlMessage = toyName;
        emlMessage = emlMessage + supplier;
        emlMessage = emlMessage + price;
        emlMessage = emlMessage + quantity;
        emlMessage = emlMessage + imageString;
        return emlMessage;
    }

    public void sendEmail(View view) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, "@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Order More Inventory");
        intent.putExtra(Intent.EXTRA_TEXT, "Toy Name:    Supplier:    Quantity:    Price:    ");

        startActivity(Intent.createChooser(intent, "Send e mail"));

    }
      private void display(int quantity) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity);
        quantityTextView.setText("" + quantity);
    }

}



















