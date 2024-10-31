package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.AddressUpdateActivity;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<Address> addressList;
    private Context context;
    private OnAddressClickListener listener;

    public AddressAdapter(List<Address> addressList, Context context, OnAddressClickListener listener) {
        this.addressList = addressList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.fullNameTextView.setText(address.getFullName());
        holder.phoneTextView.setText(address.getPhone());
        holder.addressTextView.setText(address.getWard() + ", " + address.getDistrict() + ", " + address.getCity());

        // Set RadioButton based on whether the address is the default one
        holder.radioButton.setChecked(address.isDefault());

        // Set click listener for the entire item view to open AddressUpdateActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddressUpdateActivity.class);
            intent.putExtra("addressId", address.getAddressId());
            context.startActivity(intent);
        });

        // Set listener for RadioButton
        holder.radioButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddressSelected(address); // Trigger the listener callback when a RadioButton is clicked
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTextView, phoneTextView, addressTextView;
        RadioButton radioButton;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.fullNameTextView);
            phoneTextView = itemView.findViewById(R.id.phoneTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }

    // Interface for address selection
    public interface OnAddressClickListener {
        void onAddressSelected(Address address);
    }
}
