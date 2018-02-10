package com.bferrari.inventoryapp.ui.productform;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bferrari.inventoryapp.R;
import com.bferrari.inventoryapp.data.InventoryContract;
import com.bferrari.inventoryapp.data.InventoryDBHelper;
import com.bferrari.inventoryapp.model.Product;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PRODUCT = "PRODUCT";
    private static final String LOG_TAG = FormActivity.class.getSimpleName();
    private static final int GALLERY_PICK_RESULT = 1;

    private InventoryDBHelper mDbHelper;
    private Product mProduct;

    private Toolbar mToolbar;
    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductQty;
    private EditText mSupplierEmail;
    private ImageView mProductImage;
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

        if (isEditMode()) {
            mProductName.setText(mProduct.getName());
            mProductPrice.setText(String.valueOf(mProduct.getPrice()));
            mProductQty.setText(String.valueOf(mProduct.getQty()));
            mSupplierEmail.setText(mProduct.getSupplierEmail());
            mAddButton.setText("Edit");
        }

        mAddButton.setOnClickListener(this);

        mProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK_RESULT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == GALLERY_PICK_RESULT) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                mProductImage.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, e.getMessage());

            }
        }
    }

    private void bindUi() {
        mToolbar = findViewById(R.id.toolbar);
        mProductName = findViewById(R.id.form_product_name);
        mProductPrice = findViewById(R.id.form_product_price);
        mProductQty = findViewById(R.id.form_product_quantity);
        mSupplierEmail = findViewById(R.id.form_product_supplier_email);
        mAddButton = findViewById(R.id.form_add_button);
        mProductImage = findViewById(R.id.form_product_image);
    }

    private void insertOrUpdateProduct() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.PRODUCT_NAME, mProductName.getText().toString());
        values.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, mProductPrice.getText().toString());
        values.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, mProductQty.getText().toString());
        values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_EMAIL, mSupplierEmail.getText().toString());

        if (isEditMode()) {
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

    public void increaseQty(View view) {
        int newQty = Integer.parseInt(mProductQty.getText().toString()) + 1;
        mProductQty.setText(String.valueOf(newQty));
    }

    public void decreaseQty(View view) {
        int newQty = Integer.parseInt(mProductQty.getText().toString()) - 1;
        mProductQty.setText(String.valueOf(newQty));
    }

    private boolean isEditMode() {
        return mProduct != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (isEditMode()) {
            inflater.inflate(R.menu.form_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.order_more:
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + mProduct.getSupplierEmail()));
                startActivity(Intent.createChooser(intent, "e-mail"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        if (!isEmptyField(mProductName) && !isEmptyField(mProductPrice) && !isEmptyField(mProductQty)) {
            insertOrUpdateProduct();
            finish();
        } else {
            Toast.makeText(this, R.string.blank_fields_msg, Toast.LENGTH_LONG).show();
        }
    }
}
