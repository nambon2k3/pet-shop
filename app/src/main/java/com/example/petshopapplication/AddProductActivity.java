package com.example.petshopapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petshopapplication.databinding.ActivityAddProductBinding;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.ProductDetail;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProductActivity extends AppCompatActivity {

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

        if (name.isEmpty() || importPriceStr.isEmpty() || stockStr.isEmpty() || exportPriceStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double importPrice = Double.parseDouble(importPriceStr);
        double exportPrice = Double.parseDouble(exportPriceStr);
        int stock = Integer.parseInt(stockStr);
        int height = Integer.parseInt(heightStr);
        int weight = Integer.parseInt(weightStr);

        // Create a unique key for the product
        String productUniqueKey = "product-" + databaseReferenceProducts.push().getKey();
        String productDetailUniqueKey = "product-detail-" + databaseReferenceProducts.push().getKey();
        String createdAtString = String.valueOf(System.currentTimeMillis());

        // Prepare product details
        Product product = new Product(
                productUniqueKey,
                name,
                description,
                "1", // categoryID
                "available", // status
                false,
                createdAtString, // createdAt
                1001 // createdBy
        );

        // Prepare product details
        ProductDetail productDetails = new ProductDetail(
                productDetailUniqueKey,
                productUniqueKey,
                exportPrice,
                "https://media.istockphoto.com/id/1017046150/photo/sale-shopping-dog.jpg?s=612x612&w=0&k=20&c=YxRVbcmq5BZMiurdsO3WYO-FYbUgmJG70tqUNov7wkw",
                stock,
                0,
                importPrice,
                0,
                false,
                1001,
                createdAtString, // createdAt
                height,
                weight
        );

        // Save the product to Firebase
        databaseReferenceProducts.child(productUniqueKey).setValue(product)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save product details to Firebase
                        databaseReferenceProductDetails.child(productDetailUniqueKey).setValue(productDetails)
                                .addOnCompleteListener(detailsTask -> {
                                    if (detailsTask.isSuccessful()) {
                                        Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AddProductActivity.this, "Failed to add product details", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(AddProductActivity.this, "Failed to add product", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
