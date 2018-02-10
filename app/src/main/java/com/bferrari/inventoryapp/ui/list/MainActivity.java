package com.bferrari.inventoryapp.ui.list;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bferrari.inventoryapp.R;
import com.bferrari.inventoryapp.db.InventoryContract;
import com.bferrari.inventoryapp.db.InventoryDBHelper;
import com.bferrari.inventoryapp.db.InventoryDbUtils;
import com.bferrari.inventoryapp.model.Product;
import com.bferrari.inventoryapp.ui.list.adapter.ProductsAvailableAdapter;
import com.bferrari.inventoryapp.ui.productform.FormActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private InventoryDBHelper mDbHelper;
    private List<Product> mProducts = new ArrayList<>();

    private ProductsAvailableAdapter mAdapter;

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUi();

        mDbHelper = new InventoryDBHelper(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getProducts();
        mAdapter.notifyDataSetChanged();
    }

    private void bindUi() {
        mFab = findViewById(R.id.fab);
    }

    private void setupUi() {
        setupRecyclerView();
        bindUi();

        mFab.setOnClickListener(this);
    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.my_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ProductsAvailableAdapter(this, mProducts);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getProducts() {
        Log.d(LOG_TAG, "getProducts");

        Cursor cursor = InventoryDbUtils.readProducts(mDbHelper);

        try {
            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_PRICE);
            int qtyColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_QUANTITY);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                Long currentID = cursor.getLong(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                float currentPrice = cursor.getFloat(priceColumnIndex);
                int currentQty = cursor.getInt(qtyColumnIndex);

                Product product = new Product();
                product.setId(currentID);
                product.setName(currentName);
                product.setPrice(currentPrice);
                product.setQty(currentQty);

                mProducts.add(product);

                Log.i(LOG_TAG, "ID: " + String.valueOf(currentID) +
                        " - Name: " + currentName + " - Price: " + currentPrice + " - Quantity:" + currentQty);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            cursor.close();
        }
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, FormActivity.class));
    }
}
