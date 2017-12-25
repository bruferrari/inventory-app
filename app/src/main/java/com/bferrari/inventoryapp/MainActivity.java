package com.bferrari.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private InventoryDBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new InventoryDBHelper(this);

        insertProduct();

        getProduct();
    }

    private void insertProduct() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.PRODUCT_NAME, "soap");
        values.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, "0.25");
        values.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, "10");
        values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_EMAIL, "soapsupplier@soaps.com");

        db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);

    }

    private Cursor readProduct() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.PRODUCT_NAME,
                InventoryContract.InventoryEntry.PRODUCT_PRICE,
                InventoryContract.InventoryEntry.PRODUCT_QUANTITY,
                InventoryContract.InventoryEntry.PRODUCT_IMAGE,
                InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE,
                InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_EMAIL,
        };

        return db.query(InventoryContract.InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
    }

    private void getProduct() {
        Cursor cursor = readProduct();

        try {
            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_PRICE);
            int qtyColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_QUANTITY);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentPrice = cursor.getString(priceColumnIndex);
                String currentQty = cursor.getString(qtyColumnIndex);

                Log.i(LOG_TAG, "ID: " + String.valueOf(currentID) +
                        " - Name: " + currentName + " - Price: " + currentPrice + " - Quantity:" + currentQty);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            cursor.close();
        }
    }
}
