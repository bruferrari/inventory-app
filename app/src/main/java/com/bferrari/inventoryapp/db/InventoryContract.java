package com.bferrari.inventoryapp.db;

import android.provider.BaseColumns;

public class InventoryContract {

    private InventoryContract() { }

    /**
     * Inner class that defines constant values for the habits database table.
     * Each entry in the table represents a single habit.
     */
    public static final class InventoryEntry implements BaseColumns {

        /**
         * Name of database table for habits
         */
        public final static String TABLE_NAME = "habits";

        /**
         * Unique ID number for the pet (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Description of the habit.
         * <p>
         * Type: TEXT
         */
        public final static String PRODUCT_NAME = "name";

        /**
         * Date and Time of the habit.
         * <p>
         * Type: TEXT
         */
        public final static String PRODUCT_PRICE = "price";

        public final static String PRODUCT_QUANTITY = "qty";

        public final static String PRODUCT_IMAGE = "image";

        public final static String PRODUCT_SUPPLIER_EMAIL = "suplier_email";

        public final static String PRODUCT_SUPPLIER_PHONE = "supplier_phone";
    }
}
