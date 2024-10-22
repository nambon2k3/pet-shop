package com.example.petshopapplication;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.petshopapplication.databinding.ActivityProductDetailBinding;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.ProductDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class ProductDetailActivity extends AppCompatActivity {


    ActivityProductDetailBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        String productID = "1";
        initProductDetail(productID);

    }


    private void initProductDetail(String productID) {
        reference = database.getReference(getString(R.string.tbl_product_name));
        Query query = reference.orderByChild("id").equalTo(productID);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Update product detail view
                if (snapshot.exists()) {
                    Product product = new Product();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                         product = dataSnapshot.getValue(Product.class);
                    }
                    fetchProductDetail(product);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchProductDetail(Product product) {
        reference = database.getReference(getString(R.string.tbl_product_detail_name));
        Query query = reference.orderByChild("productId").equalTo(product.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Update product detail view
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ProductDetail productDetail = dataSnapshot.getValue(ProductDetail.class);


                        binding.tvProductName.setText(product.getName());

                        //check if product is discounted
                        if(productDetail.getDiscount() > 0) {
                            binding.tvOldPrice.setText(String.format("%.1f$", productDetail.getPrice()));
                            binding.tvOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            binding.tvNewPrice.setText(String.format("%.1f$", productDetail.getPrice() * (1 - productDetail.getDiscount()/100.0)));
                            binding.tvTotalPrice.setText(String.format("%.1f$", productDetail.getPrice() * (1 - productDetail.getDiscount()/100.0)));
                        } else {
                            binding.tvOldPrice.setVisibility(View.GONE);
                            binding.tvNewPrice.setText(String.format("%.1f$", productDetail.getPrice()));
                            binding.tvTotalPrice.setText(String.format("%.1f$", productDetail.getPrice()));
                        }

                        binding.tvDescription.setText(product.getDescription());
                        binding.tvQuantity.setText("1");
                        binding.tvStock.setText("Remaining: " + productDetail.getStock());



                        Glide.with(getApplicationContext())
                                .load(productDetail.getImageUrl())
                                .transform(new CenterCrop(), new RoundedCorners(30))
                                .into(binding.imvProductImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchFeedback(Product product) {

    }

    private void fetchSuggestProduct(Product product) {

    }



}