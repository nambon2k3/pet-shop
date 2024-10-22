package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopapplication.FeedbackListActivity;
import com.example.petshopapplication.R;
import com.example.petshopapplication.UpdateFeedbackActivity;
import com.example.petshopapplication.model.FeedBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FeedBackListAdapter extends RecyclerView.Adapter<FeedBackListAdapter.FeedbackHolder> {

    List<FeedBack> feedBackItems;
    Context context;
    private String currentUserId;

    public FeedBackListAdapter(FeedbackListActivity feedbackListActivity, List<FeedBack> feedBackItems, String currentUserId) {
        this.feedBackItems = feedBackItems;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public FeedbackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.view_holder_feedback_item, parent, false);
        return new FeedbackHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackHolder holder, int position) {
        FeedBack feedback = feedBackItems.get(position);

        // Display elements of feedback
        holder.tv_feedback_user_name.setText("troll troll");
        holder.tv_feedback_content.setText(feedback.getContent());
        holder.tv_feedback_created_at.setText(feedback.getCreatedAt());
        holder.rtb_feedback_rating.setRating(feedback.getRating());

        // Load image into ImageView using Glide
        if (feedback.getImageUrl() != null && !feedback.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(feedback.getImageUrl())
                    .placeholder(R.drawable.icon) // Optional placeholder image
                    .into(holder.imv_feedback_image);
        }

        // Check if the current user matches the feedback user and if feedback is not marked as deleted
        if (feedback.getUserId().equals(currentUserId)) {
            // Add options to the spinner
            List<String> options = new ArrayList<>();
            options.add("Select Action:");
            options.add("Edit");
            options.add("Delete");

            //for marketer
//            options.add("Ban");
//            options.add("Unban");

            // Set up spinner
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, options);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.sp_feedback.setAdapter(spinnerAdapter);

            // Handle spinner item selection
            holder.sp_feedback.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    String selectedAction = (String) parentView.getItemAtPosition(position);

                    switch (selectedAction) {
                        case "Edit":
                            // Perform update feedback logic here
                            updateFeedback(feedback);
                            break;
                        case "Delete":
                            // Perform delete feedback logic here
                            deleteFeedback(feedback);
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
        } else {
            // Hide spinner if conditions are not met
            holder.sp_feedback.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return feedBackItems.size();
    }

    public class FeedbackHolder extends RecyclerView.ViewHolder {

        ImageView imv_feedback_user_avatar, imv_feedback_image;
        TextView tv_feedback_user_name, tv_feedback_content, tv_feedback_created_at;
        RatingBar rtb_feedback_rating;
        Spinner sp_feedback;

        public FeedbackHolder(@NonNull View itemView) {
            super(itemView);
            imv_feedback_user_avatar = itemView.findViewById(R.id.imv_feedback_user_avatar);
            imv_feedback_image = itemView.findViewById(R.id.imv_feedback_image);
            tv_feedback_user_name = itemView.findViewById(R.id.tv_feedback_username);
            tv_feedback_content = itemView.findViewById(R.id.tv_feedback_content);
            tv_feedback_created_at = itemView.findViewById(R.id.tv_feedback_date);
            rtb_feedback_rating = itemView.findViewById(R.id.rtb_feedback_rating);
            sp_feedback = itemView.findViewById(R.id.sp_feedback);
        }
    }

    private void updateFeedback(FeedBack feedback) {
        Intent intent = new Intent(context, UpdateFeedbackActivity.class);
        intent.putExtra("feedback", feedback);  // Pass feedback object to UpdateFeedbackActivity
        context.startActivity(intent);
    }


    private void deleteFeedback(FeedBack feedback) {
        // Set the feedback's isDeleted field to true

        feedback.setDeleted(true);

        // Save the updated feedback in Firebase (with isDeleted = true)
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks").child(feedback.getId());
        feedbackRef.setValue(feedback).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Feedback marked as deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete feedback", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
