package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.CartAdapter;
import com.example.petshopapplication.model.Cart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference cartRef; // Sử dụng đường dẫn cho giỏ hàng
    private RecyclerView recyclerView;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Khởi tạo Firebase
        database = FirebaseDatabase.getInstance();
        cartRef = database.getReference("carts"); // Đường dẫn đến giỏ hàng

        recyclerView = findViewById(R.id.rec_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy dữ liệu từ Firebase
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Cart> cartItems = new ArrayList<>();

                // Duyệt qua từng sản phẩm trong giỏ hàng
                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    Cart cartItem = cartSnapshot.getValue(Cart.class);
                    if (cartItem != null) {
                        cartItems.add(cartItem);
                    }
                }

                // Cập nhật RecyclerView
                adapter = new CartAdapter(cartItems);
                recyclerView.setAdapter(adapter);

                // Kiểm tra xem giỏ hàng có rỗng không
                if (cartItems.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Button purchaseButton = findViewById(R.id.button);
        purchaseButton.setOnClickListener(v -> {
            List<Cart> selectedItems = adapter.getSelectedItems();
            Log.d("CartActivity", "Selected items count: " + selectedItems.size());
            if (selectedItems.isEmpty()) {
                Toast.makeText(CartActivity.this, "Vui lòng chọn ít nhất một sản phẩm!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
            intent.putExtra("selectedItems", new ArrayList<>(selectedItems)); // Chuyển danh sách đã chọn
            startActivity(intent);
        });

    }
}
