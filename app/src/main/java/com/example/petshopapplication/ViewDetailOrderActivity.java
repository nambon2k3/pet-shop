package com.example.petshopapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.petshopapplication.Adapter.OrderDetailAdapter;
import com.example.petshopapplication.databinding.ActivityPrepareOrderBinding;
import com.example.petshopapplication.databinding.ActivityViewDetailOrderBinding;
import com.example.petshopapplication.model.Order;
import com.example.petshopapplication.model.OrderDetail;
import com.example.petshopapplication.utils.Validate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewDetailOrderActivity extends AppCompatActivity {
    private static final String TAG = "ViewDetailOrderActivity";
    private List<OrderDetail> orderDetailList;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private OrderDetailAdapter orderDetailAdapter;
    private ActivityViewDetailOrderBinding binding;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_detail_order);

        binding = ActivityViewDetailOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        orderId = getIntent().getStringExtra("order_id");
        Log.d(TAG, "Order ID: " + orderId);


        orderDetailList = new ArrayList<>();
        orderDetailAdapter = new OrderDetailAdapter(orderDetailList);

        binding.rcvOrderDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.rcvOrderDetails.setAdapter(orderDetailAdapter);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("orders");

        loadOrderDetailById();

        // Set click listener for back button
        binding.ivBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadOrderDetailById() {
        Log.d(TAG, "Start - load orders from Firebase");

        Query query = reference.orderByChild("id").equalTo(orderId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "Start - onDataChange");
                orderDetailList.clear();

                if (snapshot.exists()) {
                    Log.d(TAG, "Data found in Firebase");

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Order order = dataSnapshot.getValue(Order.class);
                        if (order != null) {
                            Log.d(TAG, "Order ID: " + order.getId() + ", Status: " + order.getStatus());
//                            Glide.with(ViewDetailOrderActivity.this)
//                                    .load(order.getCarrierLogo())
//                                    .into(binding.imgShipmentLogo);

                            String city = getString(R.string.petshop_address_city_description);
                            String district = getString(R.string.petshop_address_district_description);
                            String ward = getString(R.string.petshop_address_ward_description);
                            String phoneNumber = getString(R.string.petshop_phone_nunmber);

                            String addressDetail = phoneNumber + "\n" +  ward + "\n" + district + "\n" + city;

                            binding.tvAddressDetail.setText(addressDetail);

//                            binding.tvShipmentBrand.setText(order.getCarrierName());
                            binding.txtTotalPrice.setText(String.format("Total: %s", Validate.formatVND(order.getTotalAmount())));
                            // Tính tổng số sản phẩm trong order
                            int totalQuantity = 0;
                            List<OrderDetail> orderDetails = order.getOrderDetails();
                            if (orderDetails != null) {
                                for (OrderDetail detail : orderDetails) {
                                    totalQuantity += detail.getQuantity();
                                }
                            }

                            // Cập nhật TextView cho tổng số sản phẩm
//                            binding.tvProductCount.setText("Total: x" + totalQuantity + " products");

                            // Cập nhật danh sách chi tiết sản phẩm
                            orderDetailList.clear();
                            orderDetailList.addAll(order.getOrderDetails());
                        } else {
                            Log.e(TAG, "Order data is null for snapshot: " + dataSnapshot.getKey());
                        }

                    }

                    Log.e(TAG, "orderDetailList ngay sau: " + orderDetailList.toString());

                    orderDetailAdapter.notifyDataSetChanged();

                } else {
                    Log.d(TAG, "No data found in Firebase for fixed order ID: " + orderId);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load orders: " + error.getMessage());
                Toast.makeText(ViewDetailOrderActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}