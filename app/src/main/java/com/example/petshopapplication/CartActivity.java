package com.example.petshopapplication;

import android.os.Bundle;

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

public class CartActivity extends AppCompatActivity implements CartAdapter.OnItemCheckListener{
    FirebaseDatabase database;
    DatabaseReference productRef;
    DatabaseReference productDetailRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        database = FirebaseDatabase.getInstance();
        productRef = database.getReference("products");
        productDetailRef = database.getReference("product-details");

//        Cart c1 = new Cart(R.drawable.ic_launcher_background, "ten", "1.2", "1");
//        Cart c2 = new Cart(R.drawable.ic_launcher_background, "ten2", "1.3", "1");
//        Cart c3 = new Cart(R.drawable.ic_launcher_background, "ten3", "1.4", "1");




        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Cart> cartItems = new ArrayList<>();

                for(DataSnapshot proSnapshot: snapshot.getChildren()){
                    String name = proSnapshot.child("name").getValue(String.class);
                    Cart cartItem = new Cart();
                    cartItem.setImageUrl(R.drawable.ic_launcher_background);
                    cartItem.setName(name);
                    cartItem.setPrice("1.2");
                    cartItem.setQuatity("1");
                    cartItems.add(cartItem);
                }
                RecyclerView rec = findViewById(R.id.rec_cart);
                CartAdapter adapter = new CartAdapter(cartItems);
                rec.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                rec.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemCheck(Cart item) {
        //Log.d("CartActivity", "Item checked: " + item.getName());
    }

    @Override
    public void onItemUncheck(Cart item) {
        //Log.d("CartActivity", "Item unchecked: " + item.getName());
    }
}