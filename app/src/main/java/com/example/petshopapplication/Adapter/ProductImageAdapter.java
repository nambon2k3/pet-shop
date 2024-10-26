package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.model.Product;

import java.util.List;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ProductImageHolder>{

    List<Product> productItems;
    Context context;



    @NonNull
    @Override
    public ProductImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductImageHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ProductImageHolder extends RecyclerView.ViewHolder {

        public ProductImageHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
