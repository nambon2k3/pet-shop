package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.PaymentAdapter;
import com.example.petshopapplication.model.Cart;
import com.example.petshopapplication.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Cart> selectedCartItems; // Danh sách sản phẩm đã chọn
    private List<Product> productList = new ArrayList<>(); // Danh sách sản phẩm
    private TextView tvTotalPrice;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private TextView addressTextView;
    private Button changeAddressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        // Nhận dữ liệu từ CartActivity
        selectedCartItems = (ArrayList<Cart>) getIntent().getSerializableExtra("selectedItems"); // Chuyển đổi sang ArrayList
        // Khởi tạo Firebase
        database = FirebaseDatabase.getInstance();
        tvTotalPrice = findViewById(R.id.totalPriceTextView); // TextView để hiển thị tổng giá
        addressTextView = findViewById(R.id.addressTextView);
        changeAddressButton = findViewById(R.id.changeAddressButton);

        getDefaultAddress();

        // Thiết lập RecyclerView
        recyclerView = findViewById(R.id.recyclerViewProductList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tải thông tin sản phẩm
        loadProductDetails();

        changeAddressButton.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, AddressSelectionActivity.class);
            startActivity(intent);
        });
    }

    private void loadProductDetails() {
        reference = database.getReference(getString(R.string.tbl_product_name));

        // Lấy thông tin sản phẩm từ Firebase
        List<String> productIds = new ArrayList<>();
        for (Cart cart : selectedCartItems) {
            productIds.add(cart.getProductId());
        }

        reference.orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String productId = dataSnapshot.child("id").getValue(String.class);
                        if (productIds.contains(productId)) {
                            Product product = dataSnapshot.getValue(Product.class);
                            if (product != null) {
                                productList.add(product);
                            }
                        }
                    }
                    // Cập nhật adapter với danh sách sản phẩm
                    PaymentAdapter paymentAdapter = new PaymentAdapter(productList, selectedCartItems, PaymentActivity.this);
                    recyclerView.setAdapter(paymentAdapter);
                    calculateTotalPrice(); // Tính tổng giá sau khi sản phẩm đã được tải
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
                Toast.makeText(PaymentActivity.this, "Có lỗi xảy ra khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDefaultAddress() {
        DatabaseReference addressReference = database.getReference("addresses");
        String userId = "u1"; // ID của người dùng hiện tại

        addressReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                                Boolean isDefault = addressSnapshot.child("isDefault").getValue(Boolean.class);
                                String fullName = addressSnapshot.child("fullName").getValue(String.class);
                                String phone = addressSnapshot.child("phone").getValue(String.class);
                                String houseNumber = addressSnapshot.child("houseNumber").getValue(String.class);
                                String district = addressSnapshot.child("district").getValue(String.class);
                                String city = addressSnapshot.child("city").getValue(String.class);

                                if (isDefault != null && isDefault) {
                                    String address = fullName + " | " + phone + "\n" +
                                            houseNumber + ", " + district + ", " + city;
                                    Log.d("PaymentActivity", "Address found: " + address + ", isDefault: " + isDefault);
                                    addressTextView.setText(address != null ? address : "Không tìm thấy địa chỉ!");
                                    break;
                                }
                            }
                        } else {
                            Log.d("PaymentActivity", "No address found for user: " + userId);
                            addressTextView.setText("Chưa có địa chỉ nào!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PaymentActivity.this, "Có lỗi xảy ra khi lấy địa chỉ!", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void calculateTotalPrice() {
        double totalPrice = 0.0;
        for (Cart cart : selectedCartItems) {
            // Tìm sản phẩm tương ứng và tính giá
            for (Product product : productList) {
                if (product.getId().equals(cart.getProductId())) {
                    double price = product.getListVariant().get(0).getPrice();
                    int quantity = Integer.parseInt(cart.getQuantity());
                    totalPrice += price * quantity; // Tính tổng giá
                }
            }
        }
        // Cập nhật TextView với tổng giá
        tvTotalPrice.setText(String.format("Tổng tiền: %.2f$", totalPrice));
    }
}
