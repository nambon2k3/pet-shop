package com.example.petshopapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.petshopapplication.databinding.ActivityViewFeedbackItemBinding;
import com.example.petshopapplication.model.FeedBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewFeedBackItemActivity extends AppCompatActivity {

    private ActivityViewFeedbackItemBinding binding;  // ViewBinding reference
    private DatabaseReference databaseReference;
    private String productId = "p1";
    private String userId = "u1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding
        binding = ActivityViewFeedbackItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("feedbacks");

        // Fetch and display feedback for the specific product and user
        fetchFeedback("");

        // Initialize spinner options
        List<String> options = new ArrayList<>();
        options.add("Select Action:");
        options.add("Edit");
        options.add("Delete");

        // Set up spinner adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spFeedback2.setAdapter(spinnerAdapter);

        // Handle spinner item selection
        binding.spFeedback2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedAction = (String) parentView.getItemAtPosition(position);

                // Check the selected action
                switch (selectedAction) {
                    case "Edit":
                        fetchFeedback("Edit");
                        break;
                    case "Delete":
                        fetchFeedback("Delete");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No action needed
            }
        });
    }

    private void fetchFeedback(String func) {
        databaseReference.orderByChild("productId").equalTo(productId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            FeedBack feedback = dataSnapshot.getValue(FeedBack.class);
                            if (feedback != null && feedback.getUserId().equals(userId) && !feedback.isDeleted()) {
                                switch (func) {
                                    case "":
                                        displayFeedback(feedback);
                                        break;
                                    case "Edit":
                                        updateFeedback(feedback);
                                        break;
                                    case "Delete":
                                        deleteFeedback(feedback);
                                        break;
                                    default:
                                        break;
                                }
                                displayFeedback(feedback);
                                break;  // Stop after finding the matching feedback
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ViewFeedBackItemActivity.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayFeedback(FeedBack feedback) {
        binding.rtbFeedbackRating.setRating(feedback.getRating());
        binding.tvFeedbackContent.setText(feedback.getContent());
        binding.tvFeedbackDate.setText(feedback.getCreatedAt());

        // Load feedback image if available
        if (feedback.getImageUrl() != null && !feedback.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(feedback.getImageUrl())
                    .into(binding.imvFeedbackImage);
        } else {
            binding.imvFeedbackImage.setImageResource(R.drawable.icon); // Placeholder image
        }
    }

    private void updateFeedback(FeedBack feedback) {
        Intent intent = new Intent(this, UpdateFeedbackActivity.class);
        intent.putExtra("feedback", feedback);  // Pass feedback object to UpdateFeedbackActivity
        startActivity(intent);
    }

    private void deleteFeedback(FeedBack feedback) {
        feedback.setDeleted(true);

        // Save the updated feedback in Firebase (with isDeleted = true)
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks").child(feedback.getId());
        feedbackRef.setValue(feedback).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Feedback marked as deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete feedback", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
