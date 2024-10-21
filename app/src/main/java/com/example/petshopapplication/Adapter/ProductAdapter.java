package com.example.petshopapplication.Adapter;

import android.content.Context;
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
    Context context;
    FirebaseDatabase database;
    DatabaseReference reference;

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
        ProductDetail productDetail = getProductDetail(product.getId());
        holder.txt_price.setText(String.valueOf(productDetail.getPrice()));

        Glide.with(context)
                .load(productDetail.getImageUrl())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.imv_product_image);

    }


    public ProductDetail getProductDetail(String productId) {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(context.getString(R.string.tbl_product_detail_name));
        List<ProductDetail> productDetailItems = new ArrayList<>();

        // Query to find the product by productId

        return productDetailItems.get(0);
    }


    @Override
    public int getItemCount() {
        return productItems.size();
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        TextView txt_product_name, txt_price, txt_star;
        ImageView imv_product_image;


        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            txt_price = itemView.findViewById(R.id.txt_price);
            txt_star = itemView.findViewById(R.id.txt_star);
            imv_product_image = itemView.findViewById(R.id.imv_product_image);
        }
    }
}
