package com.bferrari.inventoryapp.ui.edit;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

/**
 * Created by bferrari on 12/02/18.
 */

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_PICK_RESULT = 1;
    private static final String LOG_TAG = EditActivity.class.getSimpleName();

    private InventoryDBHelper mDbHelper;
    private Product mProduct;
    private Uri mImageUri;

    private EditText mProductName;
    private EditText mProductPrice;
    private EditText mProductQty;
    private EditText mSupplierName;
    private ImageView mProductImage;
    private Button mEditButton;
    private EditText mSupplierPhone;
    private EditText mSupplierEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mDbHelper = new InventoryDBHelper(this);
        mProduct = (Product) getIntent().getSerializableExtra("PRODUCT");
        bindUi();
        setupUi();
    }

    private void setupUi() {
        mEditButton.setText("UPDATE");
        if (mProduct.getImagePath() != null) {
            mImageUri = Uri.parse(mProduct.getImagePath());

            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(Uri.parse(mProduct.getImagePath()));
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            final Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
            mProductImage.setImageBitmap(imageBitmap);
        }

        mProductName.setText(mProduct.getName());
        mProductPrice.setText(String.valueOf(mProduct.getPrice()));
        mProductQty.setText(String.valueOf(mProduct.getQty()));
        mSupplierName.setText(mProduct.getSupplierName());
        mSupplierPhone.setText(mProduct.getSupplierPhone());

        mEditButton.setOnClickListener(this);

        mProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(ACTION_OPEN_DOCUMENT,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                galleryIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
                galleryIntent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivityForResult(galleryIntent, GALLERY_PICK_RESULT);
            }
        });
    }

    private void bindUi() {
        mProductName = findViewById(R.id.form_product_name);
        mProductPrice = findViewById(R.id.form_product_price);
        mProductQty = findViewById(R.id.form_product_quantity);
        mSupplierName = findViewById(R.id.form_product_supplier_name);
        mEditButton = findViewById(R.id.form_add_button);
        mProductImage = findViewById(R.id.form_product_image);
        mSupplierPhone = findViewById(R.id.form_product_supplier_phone);
        mSupplierEmail = findViewById(R.id.form_product_supplier_email);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK_RESULT && resultCode == RESULT_OK) {
            try {
                mImageUri = data.getData();
                if (mImageUri != null) {
                    final InputStream imageStream = getContentResolver().openInputStream(mImageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    mProductImage.setImageBitmap(selectedImage);
                }
            } catch (FileNotFoundException e) {
                Log.e(LOG_TAG, e.getMessage());

            }
        }
    }

    private void updateProduct() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.PRODUCT_NAME, mProductName.getText().toString());
        values.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, mProductPrice.getText().toString());
        values.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, mProductQty.getText().toString());
        values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME, mSupplierName.getText().toString());
        values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE, mSupplierPhone.getText().toString());
        values.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_EMAIL, mSupplierEmail.getText().toString());

        if (mImageUri != null) {
            values.put(InventoryContract.InventoryEntry.PRODUCT_IMAGE, mImageUri.toString());
        }

            db.update(
                    InventoryContract.InventoryEntry.TABLE_NAME,
                    values,
                    InventoryContract.InventoryEntry._ID + "=" + mProduct.getId(),
                    null);
    }

    public void increaseQty(View view) {
        int newQty = Integer.parseInt(mProductQty.getText().toString()) + 1;
        mProductQty.setText(String.valueOf(newQty));

    }

    public void decreaseQty(View view) {
        if (Integer.parseInt(mProductQty.getText().toString()) > 0) {
            int newQty = Integer.parseInt(mProductQty.getText().toString()) - 1;
            mProductQty.setText(String.valueOf(newQty));
        }
    }

    @Override
    public void onClick(View view) {
        if (!isEmptyField(mProductName) && !isEmptyField(mProductPrice)
                && !isEmptyField(mProductQty) && !isEmptyField(mSupplierName)
                && !isEmptyField(mSupplierPhone)) {
            updateProduct();
            finish();
        } else {
            Toast.makeText(this, R.string.blank_fields_msg, Toast.LENGTH_LONG).show();
        }
    }

    private boolean isEmptyField(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }
}
