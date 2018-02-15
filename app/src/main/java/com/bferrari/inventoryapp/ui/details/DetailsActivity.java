package com.bferrari.inventoryapp.ui.details;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bferrari.inventoryapp.R;
import com.bferrari.inventoryapp.data.InventoryContract;
import com.bferrari.inventoryapp.data.InventoryDBHelper;
import com.bferrari.inventoryapp.data.InventoryDbUtils;
import com.bferrari.inventoryapp.model.Product;
import com.bferrari.inventoryapp.ui.edit.EditActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by bferrari on 12/02/18.
 */

public class DetailsActivity extends AppCompatActivity {

    private static final String PRODUCT = "PRODUCT";
    private static final String LOG_TAG = DetailsActivity.class.getSimpleName();

    private Product mProduct;
    private InventoryDBHelper mDbHelper;

    private TextView mTvProductName;
    private TextView mTvProductPrice;
    private TextView mTvProductQuantity;
    private TextView mTvProductSupplierName;
    private TextView mTvProductSupplierPhone;
    private TextView mTvProductSupplierEmail;
    private ImageView mProductImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mProduct = (Product) getIntent().getSerializableExtra(PRODUCT);
        mDbHelper = new InventoryDBHelper(this);
        bindUi();
        setData();
    }

    private void bindUi() {
        mTvProductName = findViewById(R.id.details_prod_name);
        mTvProductPrice = findViewById(R.id.details_prod_price);
        mTvProductQuantity = findViewById(R.id.details_prod_qty);
        mTvProductSupplierName = findViewById(R.id.details_prod_supplier_name);
        mTvProductSupplierEmail = findViewById(R.id.details_prod_supplier_email);
        mTvProductSupplierPhone = findViewById(R.id.details_prod_supplier_phone);
        mProductImage = findViewById(R.id.details_image);
    }

    private void setData() {
        if (mProduct != null) {
            mTvProductName.setText(mProduct.getName());
            mTvProductPrice.setText(String.valueOf(mProduct.getPrice()));
            mTvProductQuantity.setText(String.valueOf(mProduct.getQty()));
            mTvProductSupplierName.setText(mProduct.getSupplierName());
            mTvProductSupplierPhone.setText(mProduct.getSupplierPhone());
            mTvProductSupplierEmail.setText(mProduct.getSupplierEmail());

            if (mProduct.getImagePath() != null) {

                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(Uri.parse(mProduct.getImagePath()));
                } catch (FileNotFoundException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }
                final Bitmap imageBitmap = BitmapFactory.decodeStream(imageStream);
                mProductImage.setImageBitmap(imageBitmap);
            }

        }
    }

    public void removeProduct(View view) {
        final AlertDialog alertDialog = new AlertDialog.Builder(DetailsActivity.this).create();
        alertDialog.setTitle("DELETE");
        alertDialog.setMessage(getString(R.string.delete_question));

        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean status = InventoryDbUtils.deleteProduct(mDbHelper, mProduct.getId());
                if (status) {
                    finish();
                }
            }
        });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }


    public void increaseQty(View view) {
        int newQty = Integer.parseInt(mTvProductQuantity.getText().toString()) + 1;
        mTvProductQuantity.setText(String.valueOf(newQty));
        updateProduct();
    }

    public void decreaseQty(View view) {
        if (Integer.parseInt(mTvProductQuantity.getText().toString()) > 0) {
            int newQty = Integer.parseInt(mTvProductQuantity.getText().toString()) - 1;
            mTvProductQuantity.setText(String.valueOf(newQty));
        }
        updateProduct();
    }

    private void updateProduct() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, mTvProductQuantity.getText().toString());

        db.update(
                InventoryContract.InventoryEntry.TABLE_NAME,
                values,
                InventoryContract.InventoryEntry._ID + "=" + mProduct.getId(),
                null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);

        return true;
    }

    private void orderMore() {
        final AlertDialog alertDialog = new AlertDialog.Builder(DetailsActivity.this).create();
        alertDialog.setTitle("ORDER MORE");
        alertDialog.setMessage(getString(R.string.order_more));

        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "E-MAIL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",mProduct.getSupplierEmail(), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send email"));
            }
        });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "PHONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mProduct.getSupplierPhone()));
                startActivity(intent);
            }
        });

        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.order_more:
                orderMore();
                return true;
            case R.id.edit:
                if (mProduct != null) {
                    Intent editIntent = new Intent(DetailsActivity.this, EditActivity.class);
                    editIntent.putExtra(PRODUCT, mProduct);
                    startActivity(editIntent);
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
