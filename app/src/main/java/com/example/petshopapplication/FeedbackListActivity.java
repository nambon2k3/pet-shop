package com.example.petshopapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.FeedBackAdapter;
import com.example.petshopapplication.Adapter.FeedBackListAdapter;
import com.example.petshopapplication.model.FeedBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedbackListActivity extends AppCompatActivity {

    private RecyclerView rcv_feedback;
    private FeedBackListAdapter feedbackAdapter;
    private List<FeedBack> feedbackList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);

        rcv_feedback = findViewById(R.id.rcv_feedback);
        rcv_feedback.setLayoutManager(new LinearLayoutManager(this));

        feedbackList = new ArrayList<>();
        feedbackAdapter = new FeedBackListAdapter(this, feedbackList, "user--O9e3Xs72KBFUIYkldJ0");
        rcv_feedback.setAdapter(feedbackAdapter);

        // Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("feedbacks");

        // Fetch feedbacks from Firebase
        fetchFeedbacks();
    }

    private void fetchFeedbacks() {
        String productId = "p1";
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbackList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FeedBack feedback = dataSnapshot.getValue(FeedBack.class);
                    if (feedback != null && !feedback.isDeleted() && feedback.getProductId().equals(productId)) {
                        feedbackList.add(feedback); // Add feedback to the list
                    }
                }
                feedbackAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FeedbackListActivity.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}