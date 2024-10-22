package com.example.petshopapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petshopapplication.model.FeedBack;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.UUID;

public class AddFeedbackActivity extends AppCompatActivity {
    private EditText edt_feedback_comment;
    private RatingBar rb_feedback_rating;
    private ImageView imv_feedback_image;
    private Button btn_feedback_pick, btn_feedback_submit;

    private Uri selectedImageUri; // To store the selected image URI
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_feedback);

        //edt_username = findViewById(R.id.edt_login_username);
        edt_feedback_comment = findViewById(R.id.edt_feedback_comment);
        rb_feedback_rating = findViewById(R.id.rb_feedback_rating);
        imv_feedback_image = findViewById(R.id.imv_feedback_image);
        btn_feedback_pick = findViewById(R.id.btn_feedback_pick);
        btn_feedback_submit = findViewById(R.id.btn_feedback_submit);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Handle image selection
        btn_feedback_pick.setOnClickListener(view -> {
            chooseImage();
        });

        // Handle feedback submission
        btn_feedback_submit.setOnClickListener(view -> {
            uploadFeedback();
        });
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
            imv_feedback_image.setImageURI(selectedImageUri); // Display the selected image
        }
    }

    private void uploadFeedback() {
        final String userId = "user--O9e3Xs72KBFUIYkldJ0";
        final String productId = "p1";
        final String comment = edt_feedback_comment.getText().toString().trim();
        final int rating = (int) rb_feedback_rating.getRating();

        if (comment.isEmpty() || rating == 0) {
            Toast.makeText(this, "Please provide a comment and rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            // Upload the image to Firebase Storage
            StorageReference storageReference = firebaseStorage.getReference().child("feedback_images/" + UUID.randomUUID().toString());
            storageReference.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the image download URL
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // After image upload, submit the feedback to the database
                            submitFeedback(userId, productId, comment, (int) rating, imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddFeedbackActivity.this, "Image upload failed!", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // If no image is selected, submit feedback without image
            submitFeedback(userId, productId, comment, (int) rating, null);
        }
    }

    private void submitFeedback(String userId, String productId, String comment, int rating, @Nullable String imageUrl) {
        DatabaseReference feedbackRef = firebaseDatabase.getReference("feedbacks");

        String feedbackId = "feedback-" +  feedbackRef.push().getKey(); // Generate a unique ID

        // Create a feedback object
        FeedBack feedback = FeedBack.builder()
                .id(feedbackId)
                .userId(userId)
                .productId(productId)
                .rating(rating)
                .imageUrl(imageUrl)
                .content(comment)
                .createdAt(String.valueOf(new Date()))
                .build();

        // Store the feedback in the database
        feedbackRef.child(feedbackId).setValue(feedback)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddFeedbackActivity.this, "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddFeedbackActivity.this, "Failed to submit feedback.", Toast.LENGTH_SHORT).show();
                });
    }
}