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
import com.example.petshopapplication.model.ProductDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends  RecyclerView.Adapter<ProductAdapter.ProductHolder>{

    List<Product> productItems;
    List<ProductDetail> productDetailItems;
    Context context;

    public ProductAdapter(List<Product> productItems, List<ProductDetail> productDetailItems) {
        this.productItems = productItems;
        this.productDetailItems = productDetailItems;
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
        ProductDetail productDetail = getProductDetail(product.getId());
        holder.txt_product_name.setText(product.getName());
        //Check if product is discounted
        if(productDetail.getDiscount() > 0) {
            holder.tv_discount.setText(-1 * productDetail.getDiscount() + "%");
        } else {
            holder.tv_discount.setVisibility(View.GONE);
        }


        holder.tv_old_price.setText(String.valueOf(productDetail.getPrice())+"$");
        holder.tv_old_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        holder.txt_price.setText(String.format("%.2f$",productDetail.getPrice() * ( 1- productDetail.getDiscount()/100.0)));

        Glide.with(context)
                .load(productDetail.getImageUrl())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.imv_product_image);

    }

    @Override
    public int getItemCount() {
        return productDetailItems.size();
    }

    public ProductDetail getProductDetail(String productId) {
        for (ProductDetail productDetail : productDetailItems) {
            if (productDetail.getProductId().equals(productId)) {
                return productDetail;
            }
        }
        return null;
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        TextView txt_product_name, txt_price, txt_star, tv_discount, tv_old_price;
        ImageView imv_product_image;


        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            tv_discount = itemView.findViewById(R.id.tv_discount);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_price = itemView.findViewById(R.id.txt_price);
            txt_star = itemView.findViewById(R.id.txt_star);
            imv_product_image = itemView.findViewById(R.id.imv_product_image);
            tv_old_price = itemView.findViewById(R.id.tv_old_price);
        }
    }
}
