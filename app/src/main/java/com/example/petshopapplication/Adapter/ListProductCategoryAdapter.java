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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Category;

import java.util.List;

public class ListProductCategoryAdapter extends RecyclerView.Adapter<ListProductCategoryAdapter.CategoryHolder>{

    List<Category> categoryItems;
    Context context;

    public ListProductCategoryAdapter(List<Category> categoryItems) {
        this.categoryItems = categoryItems;
    }


    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_category_list_product, parent, false);
        return new ListProductCategoryAdapter.CategoryHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        Category category = categoryItems.get(position);
        holder.txt_cate_name.setText(category.getName());

        Glide.with(context)
                .load(category.getImage())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.imv_cate_image);
    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }

    class CategoryHolder extends RecyclerView.ViewHolder{

        ImageView imv_cate_image;
        TextView txt_cate_name;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            imv_cate_image = itemView.findViewById(R.id.imv_cate_image);
            txt_cate_name = itemView.findViewById(R.id.txt_cate_name);
        }
    }
}
