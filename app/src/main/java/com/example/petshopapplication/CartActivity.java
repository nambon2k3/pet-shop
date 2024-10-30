package com.example.petshopapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.CartAdapter;
import com.example.petshopapplication.model.Cart;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.Variant;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        //Get Id of current User (Fake Id)
        String userId = "u1";

        //Initialize firebase
        database = FirebaseDatabase.getInstance();

        initCart(userId);
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
        calculateTotalPrice();
    }

    public void calculateTotalPrice() {
        double totalPrice = 0.;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (Cart cart : cartList) {
            if (cart.isChecked()) {
                totalPrice+= getPriceForSelectedProduct(cart) * cart.getQuantity();
            }
        }
        ((TextView) findViewById(R.id.tv_total_price)).setText(currencyFormatter.format(totalPrice));
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
                        Double newPrice = oldPrice * (1 - product.getDiscount()/100.0);
                        return newPrice;

                    }

                }
            }
        }
        return null;
    }
}