package com.bferrari.inventoryapp.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by bferrari on 10/02/18.
 */

public class InventoryDbUtils {

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
}
