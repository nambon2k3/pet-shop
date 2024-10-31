package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.CartAdapter;
import com.example.petshopapplication.model.Cart;
import com.example.petshopapplication.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    private Button purchaseButton; // Nút mua hàng
    private TextView totalPriceTextView; // TextView hiển thị tổng tiền
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Get Id of current User (Fake Id)
        String userId = "u1"; // Thay đổi ID người dùng nếu cần

        // Khởi tạo Firebase
        database = FirebaseDatabase.getInstance();
        purchaseButton = findViewById(R.id.btn_purchase); // Khởi tạo nút mua hàng
        totalPriceTextView = findViewById(R.id.tv_total_price); // Khởi tạo TextView hiển thị tổng tiền

        initCart(userId); // Khởi tạo giỏ hàng

        // Thiết lập sự kiện khi nút mua hàng được nhấn
        purchaseButton.setOnClickListener(v -> {
            List<Cart> selectedItems = adapter.getSelectedItems(); // Lấy danh sách sản phẩm đã chọn
            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
            intent.putExtra("selectedItems", (ArrayList<Cart>) selectedItems); // Chuyển danh sách sản phẩm đã chọn
            startActivity(intent); // Chuyển sang PaymentActivity
        });
    }

    // Phương thức khởi tạo giỏ hàng
    public void initCart(String userId) {
        reference = database.getReference(getString(R.string.tbl_cart_name));

        // Đọc dữ liệu giỏ hàng của người dùng từ Firebase
        reference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Cart> cartList = new ArrayList<>();

                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Cart cart = dataSnapshot.getValue(Cart.class);
                                if (cart != null) {
                                    cartList.add(cart); // Thêm sản phẩm vào giỏ hàng
                                }
                            }
                        }

                        // Gọi phương thức để hiển thị sản phẩm trong giỏ hàng
                        getListProductInCart(cartList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi nếu cần
                    }
                });
    }

    // Phương thức lấy danh sách sản phẩm trong giỏ hàng
    public void getListProductInCart(List<Cart> cartList) {
        List<Product> productList = new ArrayList<>(); // Danh sách sản phẩm

        // Đọc dữ liệu sản phẩm từ Firebase
        DatabaseReference productReference = database.getReference(getString(R.string.tbl_product_name));
        productReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        if (product != null) {
                            productList.add(product); // Thêm sản phẩm vào danh sách
                        }
                    }
                }

                // Khởi tạo RecyclerView với adapter
                RecyclerView recyclerView = findViewById(R.id.rec_cart);
                recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                adapter = new CartAdapter(productList, cartList);
                recyclerView.setAdapter(adapter);
                updateTotalPrice(); // Cập nhật tổng tiền
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    // Phương thức cập nhật tổng giá
    public double updateTotalPrice() {
        double totalPrice = 0.0;
        List<Cart> selectedItems = adapter.getSelectedItems(); // Lấy danh sách sản phẩm đã chọn

        for (Cart cart : selectedItems) {
            Product product = adapter.getProductById(cart.getProductId());
            if (product != null) {
                Double price = product.getListVariant().get(0).getPrice();
                if (product.getDiscount() > 0) {
                    price = price * (1 - product.getDiscount() / 100.0); // Tính giá sau khi giảm giá
                }
                totalPrice += price * cart.getQuantity(); // Cộng thêm giá vào tổng
            }
        }

        totalPriceTextView.setText("Tổng tiền: " + String.format("%.1f$", totalPrice));

        return totalPrice;
    }

}
