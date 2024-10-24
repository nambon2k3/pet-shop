package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.PaymentAdapter;
import com.example.petshopapplication.model.Address;
import com.example.petshopapplication.model.Cart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private TextView totalPriceView;
    private TextView addressTextView; // TextView để hiển thị địa chỉ
    private FirebaseDatabase database;
    private DatabaseReference addressRef; // Tham chiếu đến địa chỉ trong cơ sở dữ liệu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        // Khởi tạo các view
        totalPriceView = findViewById(R.id.totalPriceTextView);
        addressTextView = findViewById(R.id.addressTextView); // ID của TextView

        // Khởi tạo Firebase
        database = FirebaseDatabase.getInstance();
        addressRef = database.getReference("addresses"); // Tham chiếu đến địa chỉ

        // Nhận các sản phẩm đã chọn từ intent
        List<Cart> selectedItems = (ArrayList<Cart>) getIntent().getSerializableExtra("selectedItems");

        // Tính tổng giá
        double totalPrice = 0.0;
        if (selectedItems != null) {
            for (Cart item : selectedItems) {
                totalPrice += item.getPrice(); // Tính tổng giá của các sản phẩm đã chọn
            }
        }

        // Cập nhật hiển thị tổng giá
        totalPriceView.setText("₫" + totalPrice);

        // Thiết lập RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewProductList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Tạo adapter và thiết lập dữ liệu
        PaymentAdapter paymentAdapter = new PaymentAdapter(selectedItems);
        recyclerView.setAdapter(paymentAdapter);

        // Lấy địa chỉ mặc định cho người dùng
        String userId = "user456"; // ID của người dùng hiện tại
        Log.d("PaymentActivity", "Fetching default address for user ID: " + userId);
        fetchDefaultAddress(userId);
    }

    private void fetchDefaultAddress(String userId) {
        addressRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("PaymentActivity", "Data snapshot received: " + dataSnapshot.toString());

                Address defaultAddress = null;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Address address = snapshot.getValue(Address.class);
                    if (address != null) {
                        Log.d("PaymentActivity", "Fetched Address: " + address.getFullName() + ", isDefault: " + address.isDefault());
                        if (address.isDefault() == false) {
                            defaultAddress = address;
                            Log.d("PaymentActivity", "Default address found: " + address.getFullName());
                            break; // Thoát vòng lặp nếu tìm thấy địa chỉ mặc định
                        }
                    } else {
                        Log.d("PaymentActivity", "Address is null for snapshot: " + snapshot.toString());
                    }
                }

                if (defaultAddress != null) {
                    // Định dạng chuỗi địa chỉ và đặt vào TextView
                    String addressString = defaultAddress.getFullName() + " | " + defaultAddress.getPhone() +
                            "\n" + defaultAddress.getHouseNumber() + ", " +
                            defaultAddress.getDistrict() + ", " + defaultAddress.getCity();
                    addressTextView.setText(addressString);
                } else {
                    addressTextView.setText("No default address found.");
                    Log.d("PaymentActivity", "No default address found for user ID: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PaymentActivity.this, "Error fetching address: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("PaymentActivity", "Error fetching address: " + databaseError.getMessage());
            }
        });
        Button changeAddressButton = findViewById(R.id.changeAddressButton); // ID của nút "Thay đổi địa chỉ"
        changeAddressButton.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, AddressSelectionActivity.class);
            startActivity(intent); // Mở AddressSelectionActivity
        });

    }
}
