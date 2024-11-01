package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.CartAdapter;
import com.example.petshopapplication.model.Cart;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.Variant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemCheckedListener {
    FirebaseDatabase database;
    DatabaseReference reference;
    CartAdapter.OnCartItemCheckedListener listener;
    List<Cart> cartList = new ArrayList<>();
    List<Product> productList = new ArrayList<>();
    List<String> cartProductId = new ArrayList<>();
    List<Cart> selectedItemList = new ArrayList<>();
    FirebaseAuth auth;
    FirebaseUser user;
    Button purchaseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Get Id of current User
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String userId = user.getUid();

        //Initialize firebase
        database = FirebaseDatabase.getInstance();

        initCart(userId);


        purchaseButton = findViewById(R.id.btn_purchase); // Khởi tạo nút mua hàng

        // Thiết lập sự kiện khi nút mua hàng được nhấn
        purchaseButton.setOnClickListener(v -> {
            double totalAmount =  calculateTotalPrice();
            List<Cart> selectedItems = selectedItemList; // Lấy danh sách sản phẩm đã chọn
            Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
            intent.putExtra("selectedItems", (ArrayList<Cart>) selectedItems); // Chuyển danh sách sản phẩm đã chọn
            intent.putExtra("totalAmount", totalAmount);
            startActivity(intent); // Chuyển sang PaymentActivity
        });
    }

    public void initCart(String userId) {
        reference = database.getReference(getString(R.string.tbl_cart_name));

        //Read data cart of current user from firebase
        reference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Cart cart = dataSnapshot.getValue(Cart.class);
                                cartList.add(cart);
                            }
                            getListProductInCart(cartList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void getListProductInCart(List<Cart> cartList) {

        for (Cart cart : cartList) {
            cartProductId.add(cart.getProductId());
        }

        reference = database.getReference(getString(R.string.tbl_product_name));
        reference.orderByChild("id")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                //Check if product is in cart or not
                                if (cartProductId.contains(dataSnapshot.
                                        child("id").getValue(String.class))) {
                                    productList.add(dataSnapshot.getValue(Product.class));
                                }
                            }

                            RecyclerView rec = findViewById(R.id.rec_cart);
                            CartAdapter adapter = new CartAdapter(productList, cartList, CartActivity.this, CartActivity.this);
                            rec.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                            rec.setAdapter(adapter);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    @Override
    public void onCartItemCheckedChanged() {
        updateSelectedItemList();
        calculateTotalPrice();
    }

    private void updateSelectedItemList() {
        selectedItemList.clear();  // Xóa danh sách cũ để cập nhật lại
        for (Cart cart : cartList) {
            if (cart.isChecked()) { // Chỉ thêm các Cart được chọn
                selectedItemList.add(cart);
            }
        }
    }
    public double calculateTotalPrice() { // Đổi kiểu trả về thành double
        double totalPrice = 0.;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (Cart cart : selectedItemList) { // Duyệt qua selectedItemList để tính tổng giá
            totalPrice += getPriceForSelectedProduct(cart) * cart.getQuantity();
        }

        ((TextView) findViewById(R.id.tv_total_price)).setText(currencyFormatter.format(totalPrice));
        return totalPrice; // Trả về tổng số tiền
    }

    public Double getPriceForSelectedProduct(Cart cart) {
        double price = 0.;

        //Find related product
        for (Product product : productList) {
            if (cart.getProductId().equals(product.getId())) {
                List<Variant> variantList = product.getListVariant();

                //Find related variant
                for (Variant variant : variantList) {
                    if (cart.getSelectedVariantId().equals(variant.getId())) {
                        Double oldPrice = variant.getPrice();
                        Double newPrice = oldPrice * (1 - product.getDiscount() / 100.0);
                        return newPrice;

                    }

                }
            }
        }
        return null;
    }
}