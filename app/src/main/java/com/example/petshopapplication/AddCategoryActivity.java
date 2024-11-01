package com.example.petshopapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petshopapplication.databinding.ActivityAddCategoryBinding;
import com.example.petshopapplication.databinding.ActivityAddFeedbackBinding;
import com.example.petshopapplication.model.Category;
import com.example.petshopapplication.model.FeedBack;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.UUID;

public class AddCategoryActivity extends AppCompatActivity {
    private ActivityAddCategoryBinding binding;
    private Uri selectedImageUri; // To store the selected image URI
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Initialize ViewBinding
        binding = ActivityAddCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Handle image selection
        binding.btnCategoryImagePick.setOnClickListener(view -> chooseImage());

        // Handle category submit submission
        binding.btnCategorySubmit.setOnClickListener(view -> uploadCategory());


    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityIfNeeded(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData(); // Save the selected image URI
            binding.imvCategoryImage.setImageURI(selectedImageUri); // Display the selected image
        }
    }

    private void uploadCategory() {
        final String categoryName = binding.edtCategoryName.getText().toString().trim();

        if (categoryName.isEmpty()) {
            Toast.makeText(this, "Please provide a name for category", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            // Upload the image to Firebase Storage
            StorageReference storageReference = firebaseStorage.getReference().child("category_image/" + UUID.randomUUID().toString());
            storageReference.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the image download URL
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // After image upload, submit the feedback to the database
                            addCategory(categoryName, imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddCategoryActivity.this, "Image upload failed!", Toast.LENGTH_SHORT).show());
        }
//        else {
//            // If no image is selected, submit feedback without image
//            submitFeedback(userId, productId, comment, rating, null);
//        }
    }

    private void addCategory(String categoryName, String imageUrl) {
        DatabaseReference categoryRef = firebaseDatabase.getReference("categories");

        String categoryId = "category-" + categoryRef.push().getKey(); // Generate a unique ID

        // Create a category object
        Category category = Category.builder()
                .id(categoryId)
                .name(categoryName)
                .image(imageUrl)
                .createdAt(String.valueOf(new Date()))
                .build();

        // Store the category in the database
        categoryRef.child(categoryId).setValue(category)
                .addOnSuccessListener(aVoid -> Toast.makeText(AddCategoryActivity.this, "Category added successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AddCategoryActivity.this, "Failed to add category.", Toast.LENGTH_SHORT).show());
        Intent intent = new Intent(AddCategoryActivity.this, CategoryListActivity.class);
        startActivity(intent);
    }
}