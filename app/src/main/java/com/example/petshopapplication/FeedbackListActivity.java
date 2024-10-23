package com.example.petshopapplication;

import android.os.Bundle;
import android.view.View;
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
import com.example.petshopapplication.databinding.ActivityFeedbackListBinding;
import com.example.petshopapplication.model.FeedBack;
import com.example.petshopapplication.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedbackListActivity extends AppCompatActivity {

    private ActivityFeedbackListBinding binding;  // ViewBinding reference
    private FeedBackListAdapter feedbackAdapter;
    private List<FeedBack> feedbackList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewBinding
        binding = ActivityFeedbackListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up RecyclerView
        binding.rcvFeedback.setLayoutManager(new LinearLayoutManager(this));

        feedbackList = new ArrayList<>();
        binding.rcvFeedback.setAdapter(feedbackAdapter);

        // Firebase Realtime Database reference
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("feedbacks");

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
                        System.out.println(feedback);
                        feedbackList.add(feedback); // Add feedback to the list
                    }
                }
                fetchUserData(feedbackList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FeedbackListActivity.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserData(List<FeedBack> feedbackItems) {
        databaseReference = database.getReference(getString(R.string.tbl_user_name));
        List<User> userItems = new ArrayList<>();
        for(FeedBack feedBack : feedbackItems) {
            //Reference to the user table
            databaseReference = database.getReference(getString(R.string.tbl_user_name));
            //Get user data by user Id in feed back
            Query query = databaseReference.orderByChild("id").equalTo(feedBack.getUserId());
            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    System.out.println("go");
                    if(snapshot.exists()) {
                        System.out.println("go");
                        //Get user data from database
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            System.out.println("go");
                            User user = dataSnapshot.getValue(User.class);
                            System.out.println(user);
                            userItems.add(user);
                        }
                        if(!userItems.isEmpty()) {
                            feedbackAdapter = new FeedBackListAdapter(feedbackItems, userItems);
                            binding.rcvFeedback.setLayoutManager(new LinearLayoutManager(FeedbackListActivity.this, RecyclerView.VERTICAL, true));
                            binding.rcvFeedback.setAdapter(feedbackAdapter);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}