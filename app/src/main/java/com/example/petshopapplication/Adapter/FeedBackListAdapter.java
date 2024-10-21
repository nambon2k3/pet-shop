package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopapplication.FeedbackListActivity;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.FeedBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FeedBackListAdapter extends RecyclerView.Adapter<FeedBackListAdapter.FeedbackHolder>{

    List<FeedBack> feedBackItems;
    Context context;

    public FeedBackListAdapter(FeedbackListActivity feedbackListActivity, List<FeedBack> feedBackItems) {
        this.feedBackItems = feedBackItems;
    }

    @NonNull
    @Override
    public FeedbackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.view_holder_feedback, parent, false);
        return new FeedbackHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackHolder holder, int position) {
        FeedBack feedback = feedBackItems.get(position);
        //holder.tv_feedback_user_name.setText();
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
    }

    @Override
    public int getItemCount() {
        return feedBackItems.size();
    }

    public class FeedbackHolder extends RecyclerView.ViewHolder {

        ImageView imv_feedback_user_avatar, imv_feedback_image;
        TextView tv_feedback_user_name, tv_feedback_content, tv_feedback_created_at;
        RatingBar rtb_feedback_rating;

        public FeedbackHolder(@NonNull View itemView) {
            super(itemView);
            imv_feedback_user_avatar = itemView.findViewById(R.id.imv_feedback_user_avatar);
            imv_feedback_image = itemView.findViewById(R.id.imv_feedback_image);
            tv_feedback_user_name = itemView.findViewById(R.id.tv_feedback_username);
            tv_feedback_content = itemView.findViewById(R.id.tv_feedback_content);
            tv_feedback_created_at = itemView.findViewById(R.id.tv_feedback_date);
            rtb_feedback_rating = itemView.findViewById(R.id.rtb_feedback_rating);
        }
    }
}
