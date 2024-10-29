package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
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
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private TextView tvTotalPrice;
    private List<Cart> cartList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private List<Boolean> selectedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        tvTotalPrice = findViewById(R.id.tv_total_price);
        Button btnPurchase = findViewById(R.id.btn_purchase);
        String userId = "u1"; // ID của người dùng hiện tại

        database = FirebaseDatabase.getInstance();
        initCart(userId);

        btnPurchase.setOnClickListener(v -> {
            List<Cart> selectedCartItems = getSelectedCartItems();
            // Chuyển sang PaymentActivity và truyền dữ liệu
            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
            intent.putExtra("selectedItems", (ArrayList<Cart>) selectedCartItems); // Chuyển đổi sang ArrayList
            startActivity(intent);
        });
    }

    private List<Cart> getSelectedCartItems() {
        List<Cart> selectedCartItems = new ArrayList<>();
        for (int i = 0; i < cartList.size(); i++) {
            if (selectedItems.get(i)) {
                selectedCartItems.add(cartList.get(i));
            }
        }
        return selectedCartItems;
    }

    public void calculateTotalPrice() {
        double totalPrice = 0.0;
        for (int i = 0; i < cartList.size(); i++) {
            if (selectedItems.get(i)) {
                Cart cart = cartList.get(i);
                Product product = getProductById(cart.getProductId());
                if (product != null) {
                    double price = product.getListVariant().get(0).getPrice();
                    int quantity = Integer.parseInt(cart.getQuatity());
                    totalPrice += price * quantity;
                }
            }
        }
        tvTotalPrice.setText(String.format("%.2f$", totalPrice));
    }

    private Product getProductById(String productId) {
        for (Product product : productList) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    public void initCart(String userId) {
        reference = database.getReference(getString(R.string.tbl_cart_name));
        reference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            loadCartItems(snapshot);
                        } else {
                            tvTotalPrice.setText("0.00$");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi
                    }
                });
    }

    private void loadCartItems(DataSnapshot snapshot) {
        cartList.clear();
        selectedItems.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Cart cart = dataSnapshot.getValue(Cart.class);
            if (cart != null) {
                cartList.add(cart);
                selectedItems.add(false);
            }
        }
        getListProductInCart(cartList);
    }

    public void getListProductInCart(List<Cart> cartList) {
        List<String> cartProductId = new ArrayList<>();
        for (Cart cart : cartList) {
            cartProductId.add(cart.getProductId());
        }

        reference = database.getReference(getString(R.string.tbl_product_name));
        reference.orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loadProducts(snapshot, cartProductId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }

    private void loadProducts(DataSnapshot snapshot, List<String> cartProductId) {
        productList.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            String productId = dataSnapshot.child("id").getValue(String.class);
            if (cartProductId.contains(productId)) {
                productList.add(dataSnapshot.getValue(Product.class));
            }
        }

        setupRecyclerView();
        calculateTotalPrice();
    }

    private void setupRecyclerView() {
        RecyclerView rec = findViewById(R.id.rec_cart);
        CartAdapter adapter = new CartAdapter(productList, cartList, this, selectedItems);
        rec.setLayoutManager(new LinearLayoutManager(this));
        rec.setAdapter(adapter);
    }
}
