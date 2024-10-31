package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petshopapplication.API_model.Rate;
import com.example.petshopapplication.R;
import java.util.List;

// Trong RateAdapter.java
public class RateAdapter extends RecyclerView.Adapter<RateAdapter.RateViewHolder> {

    private List<Rate> rateList;
    private Context context;
    private OnRateSelectListener listener;

    public interface OnRateSelectListener {
        void onRateSelected(double fee, boolean isSelected);
    }

    public void setRateList(List<Rate> rateList) {
        this.rateList = rateList;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }


    public RateAdapter(List<Rate> rateList, Context context, OnRateSelectListener listener) {
        this.rateList = rateList;
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rate, parent, false);
        return new RateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RateViewHolder holder, int position) {
        Rate rate = rateList.get(position);
        holder.carrierNameTextView.setText(rate.getCarrierName());
        holder.feeTextView.setText(String.format("%s", rate.getTotalAmount()));

        holder.selectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onRateSelected(rate.getTotalAmount(), isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    public static class RateViewHolder extends RecyclerView.ViewHolder {
        TextView carrierNameTextView, feeTextView;
        CheckBox selectCheckBox;

        public RateViewHolder(@NonNull View itemView) {
            super(itemView);
            carrierNameTextView = itemView.findViewById(R.id.carrierNameTextView);
            feeTextView = itemView.findViewById(R.id.feeTextView);
            selectCheckBox = itemView.findViewById(R.id.selectCheckBox);
        }
    }
}

