package com.bferrari.inventoryapp.ui.productform;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bferrari.inventoryapp.R;
import com.bferrari.inventoryapp.db.InventoryContract;
import com.bferrari.inventoryapp.db.InventoryDBHelper;
import com.bferrari.inventoryapp.model.Product;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PRODUCT = "PRODUCT";

    private InventoryDBHelper mDbHelper;
    private Product mProduct;

    private Toolbar mToolbar;
    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductQty;
    private EditText mSupplierEmail;
    private Button mAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mDbHelper = new InventoryDBHelper(this);

        mProduct = (Product) getIntent().getSerializableExtra(PRODUCT);

        setupUi();
    }

    private void setupUi() {
        bindUi();
        setSupportActionBar(mToolbar);

        if (mProduct != null) {
            mProductName.setText(mProduct.getName());
            mProductPrice.setText(String.valueOf(mProduct.getPrice()));
            mProductQty.setText(String.valueOf(mProduct.getQty()));
            mAddButton.setText("Edit");
        }

        mAddButton.setOnClickListener(this);
    }

    private void bindUi() {
        mToolbar = findViewById(R.id.toolbar);
        mProductName = findViewById(R.id.form_product_name);
        mProductPrice = findViewById(R.id.form_product_price);
        mProductQty = findViewById(R.id.form_product_quantity);
        mSupplierEmail = findViewById(R.id.form_product_supplier_email);
        mAddButton = findViewById(R.id.form_add_button);
    }

    private void insertOrUpdateProduct() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.PRODUCT_NAME, mProductName.getText().toString());
        values.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, mProductPrice.getText().toString());
        values.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, mProductQty.getText().toString());
        values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_EMAIL, mSupplierEmail.getText().toString());

        if (mProduct != null) {
            db.update(
                    InventoryContract.InventoryEntry.TABLE_NAME,
                    values,
                    InventoryContract.InventoryEntry._ID + "=" + mProduct.getId(),
                    null);
        } else {
            db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        }
    }

    private boolean isEmptyField(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    @Override
    public void onClick(View view) {
        if (!isEmptyField(mProductName) && !isEmptyField(mProductPrice) && !isEmptyField(mProductQty)) {
            insertOrUpdateProduct();
            finish();
        } else {
            Toast.makeText(this, "blank fields", Toast.LENGTH_LONG).show();
        }
    }
}
