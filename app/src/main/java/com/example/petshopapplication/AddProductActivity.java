package com.example.petshopapplication;

import android.os.Bundle;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.petshopapplication.databinding.ActivityAddProductBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ActivityAddProductBinding binding;
    private DatabaseReference databaseReferenceProducts, databaseReferenceProductDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using ViewBinding
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Database references
        databaseReferenceProducts = FirebaseDatabase.getInstance().getReference("products");
        databaseReferenceProductDetails = FirebaseDatabase.getInstance().getReference("productdetails");

        // Set OnClickListener for the Add Product button
        binding.addProductButton.setOnClickListener(v -> addProduct());
    }

    private void addProduct() {
        String name = binding.addProductName.getText().toString().trim();
        String importPriceStr = binding.addProductImportPrice.getText().toString().trim();
        String exportPriceStr = binding.addProductExportPrice.getText().toString().trim();
        String stockStr = binding.addProductStock.getText().toString().trim();
        String description = binding.addProductDescription.getText().toString().trim();
        String heightStr = binding.addProductHeight.getText().toString().trim();
        String weightStr = binding.addProductWeight.getText().toString().trim();
        String lengthStr = binding.addProductLength.getText().toString().trim();
        String widthStr = binding.addProductWidth.getText().toString().trim();

        if (name.isEmpty() || importPriceStr.isEmpty() || stockStr.isEmpty() || exportPriceStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double importPrice = Double.parseDouble(importPriceStr);
        double exportPrice = Double.parseDouble(exportPriceStr);
        int stock = Integer.parseInt(stockStr);
        int height = Integer.parseInt(heightStr);
        int weight = Integer.parseInt(weightStr);
        int length = Integer.parseInt(lengthStr);
        int width = Integer.parseInt(widthStr);

        // Create a unique key for the product
        String productUniqueKey = "product-" + databaseReferenceProducts.push().getKey();
        String productDetailUniqueKey = "product-detail-" + databaseReferenceProducts.push().getKey();
        String createdAtString = String.valueOf(System.currentTimeMillis());

    }





}
