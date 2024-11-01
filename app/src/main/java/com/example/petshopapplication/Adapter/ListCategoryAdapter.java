package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListCategoryAdapter extends RecyclerView.Adapter<ListCategoryAdapter.CategoryHolder> {
    List<Category> categoryList = new ArrayList<>();
    Context context;


    public ListCategoryAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public ListCategoryAdapter.CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_category_item, parent, false);
        return new ListCategoryAdapter.CategoryHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCategoryAdapter.CategoryHolder holder, int position) {
        //List<Category> categoryList = new ArrayList<>(categoryIntegerMap.keySet());
        Category category = categoryList.get(position);

        Glide.with(context)
                .load(category.getImage())
                .into(holder.imv_category);
        holder.tv_category_name.setText(category.getName());
        //holder.tv_product_quantity.setText(categoryIntegerMap.get(category).toString());

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        ImageView imv_category;
        TextView tv_category_name, tv_product_quantity, tv_category_quantity;
        public CategoryHolder(@NonNull View itemView) {
            super(itemView);

            imv_category = itemView.findViewById(R.id.imv_category);
            tv_category_name = itemView.findViewById(R.id.tv_category_name);
            tv_product_quantity = itemView.findViewById(R.id.tv_product_quantity);
        }
    }
}
