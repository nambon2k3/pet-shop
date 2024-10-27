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