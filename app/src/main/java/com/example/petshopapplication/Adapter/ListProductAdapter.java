package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.graphics.Paint;
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
import com.example.petshopapplication.model.ProductDetail;

import java.util.List;

public class ListProductAdapter extends RecyclerView.Adapter<ListProductAdapter.ProductHolder>{

    List<Product> productItems;
    List<ProductDetail> productDetailItems;
    Context context;

    public ListProductAdapter(List<Product> productItems, List<ProductDetail> productDetailItems) {
        this.productItems = productItems;
        this.productDetailItems = productDetailItems;
    }


    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.view_holder_list_product, parent, false);
        return new ProductHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        Product product = productItems.get(position);
        ProductDetail productDetail = getProductDetail(product.getId());
        holder.txt_product_name.setText(product.getName());
        //Check if product is discounted
        if(productDetail.getDiscount() > 0) {
            //holder.tv_discount.setText(-1 * productDetail.getDiscount() + "%");
            holder.tv_old_price.setVisibility(View.VISIBLE);
            holder.tv_new_price.setVisibility(View.VISIBLE);
            holder.tv_new_price.setText(String.format("%.2f", productDetail.getPrice() - (productDetail.getPrice() * productDetail.getDiscount() / 100)));
            holder.tv_old_price.setText(String.format("%.2f", productDetail.getPrice()));
            holder.tv_old_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        } else {
            holder.tv_discount.setVisibility(View.GONE);
            holder.tv_old_price.setVisibility(View.VISIBLE);
            holder.tv_new_price.setVisibility(View.GONE);
            holder.tv_old_price.setText(String.format("%.2f", productDetail.getPrice()));
        }
        Glide.with(context)
                .load(productDetail.getImageUrl())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.imv_product_image);
        //holder.txt_rating.setText(product.getRating());
    }

    public ProductDetail getProductDetail(String productId) {
        for (ProductDetail productDetail : productDetailItems) {
            if (productDetail.getProductId().equals(productId)) {
                return productDetail;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return productItems.size();
    }


    public class ProductHolder extends RecyclerView.ViewHolder {
        ImageView imv_product_image;
        TextView txt_product_name, tv_rating, tv_discount, tv_old_price, tv_new_price;
        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            imv_product_image = itemView.findViewById(R.id.imv_product_image);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            tv_new_price = itemView.findViewById(R.id.tv_new_price);
            tv_old_price = itemView.findViewById(R.id.tv_old_price);
            tv_rating = itemView.findViewById(R.id.tv_rating);


        }
    }



}
