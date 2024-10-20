package com.example.petshopapplication.Adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    List<Category> categoryItems;
    Context context;

    public CategoryAdapter(List<Category> categoryItems) {
        this.categoryItems = categoryItems;
    }


    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        //View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_product);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
