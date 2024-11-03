package com.example.petshopapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.petshopapplication.Adapter.OrderAdapter;
import com.example.petshopapplication.databinding.FragmentAllOrdersTablayoutBinding;
import com.example.petshopapplication.databinding.FragmentProcessingTablayoutBinding;
import com.example.petshopapplication.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.NonNull;

public class AllOrdersTablayoutFragment extends Fragment {
    private static final String TAG = "CityActivity";
    private FragmentAllOrdersTablayoutBinding binding;
    private OrderAdapter adapter;
    private List<Order> orderItems;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private boolean isInventory;

    public AllOrdersTablayoutFragment(boolean isInventory) {
        this.isInventory = isInventory;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllOrdersTablayoutBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        orderItems = new ArrayList<>();
        adapter = new OrderAdapter(orderItems, false, "all", isInventory);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("orders");

        initOrders();
        return view;
    }

    // Get All orders for Inventory:
    private void initOrdersInventory() {
        Log.d(TAG, "Start - load orders from Firebase");

        Query query = reference; // Sử dụng query với điều kiện lọc isDeleted là false
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
            Log.d(TAG, "Start - onDataChange");
                orderItems.clear(); // Xóa dữ liệu cũ

                // Kiểm tra xem snapshot có dữ liệu hay không
                if (snapshot.exists()) {
                    Log.d(TAG, "Data found in Firebase");

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Order order = dataSnapshot.getValue(Order.class);
                        if (order != null) {
                            Log.d(TAG, "Order ID: " + order.getId() + ", Status: " + order.getStatus());
                            orderItems.add(order);
                        } else {
                            Log.e(TAG, "Order data is null for snapshot: " + dataSnapshot.getKey());
                        }
                    }

                    Log.d(TAG, "Total orders retrieved: " + orderItems.size());
                    adapter.notifyDataSetChanged(); // Cập nhật RecyclerView với dữ liệu mới

                } else {
                    Log.d(TAG, "No data found in Firebase");
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load orders: " + error.getMessage());
                Toast.makeText(getContext(), "Không tải được danh sách đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d(TAG, "End - load orders from Firebase");
    }

    // Get All orders for User:
    private void initOrders() {
        Log.d(TAG, "Start - load orders from Firebase");

        // Get current userId
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "User is not logged in.");
            Toast.makeText(getContext(), "User is not logged in.", Toast.LENGTH_SHORT).show();
            return; // Stop further execution if user is not logged in
        }

        String userId = currentUser.getUid();
        Log.d(TAG, "Loading orders for user ID: " + userId);

        // Get All Orders for current user
        Query query = reference.orderByChild("userId").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Start - onDataChange");
                orderItems.clear(); // Clear old data

                // Check if snapshot has data
                if (snapshot.exists()) {
                    Log.d(TAG, "Data found in Firebase");

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Order order = dataSnapshot.getValue(Order.class);
                        if (order != null) {
                            Log.d(TAG, "Order ID: " + order.getId() + ", Status: " + order.getStatus());
                            orderItems.add(order);
                        } else {
                            Log.e(TAG, "Order data is null for snapshot: " + dataSnapshot.getKey());
                        }
                    }

                    Log.d(TAG, "Total orders retrieved: " + orderItems.size());
                    adapter.notifyDataSetChanged(); // Update RecyclerView with new data

                } else {
                    Log.d(TAG, "No data found in Firebase for user ID: " + userId);
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load orders: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d(TAG, "End - load orders from Firebase");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // destroy binding view when fragment is destroyed
    }
}
