package com.bferrari.inventoryapp.ui.list.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bferrari.inventoryapp.R;
import com.bferrari.inventoryapp.db.InventoryDBHelper;
import com.bferrari.inventoryapp.db.InventoryDbUtils;
import com.bferrari.inventoryapp.model.Product;
import com.bferrari.inventoryapp.ui.productform.FormActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bferrari on 10/02/18.
 */

public class ProductsAvailableAdapter
        extends RecyclerView.Adapter<ProductsAvailableAdapter.ViewHolder> {

    private static final String LOG_TAG = ProductsAvailableAdapter.class.getSimpleName();

    private List<Product> mProducts = new ArrayList<>();
    private InventoryDBHelper mDbHelper;
    private Context mContext;

    public ProductsAvailableAdapter(Context context, List<Product> products) {
        mProducts = products;
        mContext = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView price;
        TextView qty;
        ImageView trashBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            qty = itemView.findViewById(R.id.product_qty);
            trashBtn = itemView.findViewById(R.id.trash_btn);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mDbHelper = new InventoryDBHelper(mContext);

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Product product = mProducts.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(String.valueOf(product.getPrice()));
        holder.qty.setText(String.valueOf(product.getQty()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product product = mProducts.get(position);
                Log.d(LOG_TAG, "" + product.toString());

                Intent intent = new Intent(mContext, FormActivity.class);
                intent.putExtra("PRODUCT", product);

                mContext.startActivity(intent);
            }
        });

        holder.trashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean status = InventoryDbUtils.deleteProduct(mDbHelper, product.getId());
                if (status) {
                    mProducts.remove(holder.getPosition());
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

}
