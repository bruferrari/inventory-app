package com.bferrari.inventoryapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bferrari.inventoryapp.model.Product;

/**
 * Created by bferrari on 10/02/18.
 */

public class InventoryDbUtils {

    private static final String LOG_TAG = InventoryDbUtils.class.getSimpleName();

    private InventoryDbUtils() { }

    public static boolean deleteProduct(InventoryDBHelper dbHelper, Long productId) {
        return dbHelper.getReadableDatabase()
                .delete(InventoryContract.InventoryEntry.TABLE_NAME,
                        InventoryContract.InventoryEntry._ID + "=" + productId,
                        null) > 0;
    }

    public static Cursor readProducts(InventoryDBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.PRODUCT_NAME,
                InventoryContract.InventoryEntry.PRODUCT_PRICE,
                InventoryContract.InventoryEntry.PRODUCT_QUANTITY,
                InventoryContract.InventoryEntry.PRODUCT_IMAGE,
                InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME,
                InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE
        };

        return db.query(InventoryContract.InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
    }

    public static void updateSaleOnProduct(InventoryDBHelper dbHelper, Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.PRODUCT_NAME, product.getName());
        values.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, product.getPrice());
        values.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, product.getQty());
        values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME, product.getSupplierName());
        values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE, product.getSupplierPhone());

        if (product.getImagePath() != null) {
            values.put(InventoryContract.InventoryEntry.PRODUCT_IMAGE, product.getImagePath());
        }

        if (product != null) {
            db.update(
                    InventoryContract.InventoryEntry.TABLE_NAME,
                    values,
                    InventoryContract.InventoryEntry._ID + "=" + product.getId(),
                    null);
            Log.d(LOG_TAG, "Product updated!" + product.toString());
        }
    }

    public static void eraseTable(InventoryDBHelper dbHelper) {
        dbHelper.getReadableDatabase().execSQL("delete from " + InventoryContract.InventoryEntry.TABLE_NAME);
    }
}
