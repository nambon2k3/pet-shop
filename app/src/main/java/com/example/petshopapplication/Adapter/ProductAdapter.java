package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
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
import com.example.petshopapplication.model.Product;
import java.util.List;

public class ProductAdapter extends  RecyclerView.Adapter<ProductAdapter.ProductHolder>{

    List<Product> productItems;
    Context context;

    public ProductAdapter(List<Product> productItems) {
        this.productItems = productItems;
    }


    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflater = LayoutInflater.from(context).inflate(R.layout.view_holder_product, parent, false);
        return new ProductHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        Product product = productItems.get(position);
        holder.txt_product_name.setText(product.getName());

        double oldPrice = product.getBasePrice();
        String imageUrl = product.getBaseImageURL();

        //Check if product have variants
        if(!product.getListVariant().isEmpty()) {
            oldPrice = product.getListVariant().get(0).getPrice();
            //check if product have color variants
            if(!product.getListVariant().get(0).getListColor().isEmpty()) {
                imageUrl = product.getListVariant().get(0).getListColor().get(0).getImageUrl();
            }
        }

        //check if product is discounted
        if(product.getDiscount() > 0) {
            holder.tv_discount.setText(String.valueOf("-" + product.getDiscount()) + "%");
            holder.tv_old_price.setText(String.format("%.1f", oldPrice));
            //holder.tv_old_price.
            holder.tv_new_price.setText(String.format("%.1f", oldPrice * (1 - product.getDiscount()/100.0)));

        } else {
            holder.tv_discount.setVisibility(View.GONE);
            holder.tv_old_price.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(imageUrl)
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.imv_product_image);


    }

    @Override
    public int getItemCount() {
        return productItems.size();
    }



    public class ProductHolder extends RecyclerView.ViewHolder {

        TextView txt_product_name, tv_new_price, txt_star, tv_discount, tv_old_price;
        ImageView imv_product_image;


        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            tv_discount = itemView.findViewById(R.id.tv_discount);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            tv_new_price = itemView.findViewById(R.id.txt_price);
            txt_star = itemView.findViewById(R.id.txt_star);
            imv_product_image = itemView.findViewById(R.id.imv_product_image);
            tv_old_price = itemView.findViewById(R.id.tv_old_price);
        }
    }
}
