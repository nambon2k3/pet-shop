package com.example.petshopapplication;

import android.os.Bundle;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity{
    FirebaseDatabase database;
    DatabaseReference reference;

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

    public void initCart(String userId){
        reference = database.getReference(getString(R.string.tbl_cart_name));

        //Read data cart of current user from firebase
        reference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Cart> cartList = new ArrayList<>();

                if(snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Cart cart = dataSnapshot.getValue(Cart.class);
                        cartList.add(cart);

                        getListProductInCart(cartList);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getListProductInCart(List<Cart> cartList){
        List<Product> productList = new ArrayList<>();
        List<String> cartProductId = new ArrayList<>();

        for(Cart cart: cartList){
            cartProductId.add(cart.getProductId());
        }

        reference = database.getReference(getString(R.string.tbl_product_name));
            reference.orderByChild("id")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                            //check if product is in cart or not
                            if(cartProductId.contains(dataSnapshot.
                                    child("id").getValue(String.class))){
                                productList.add(dataSnapshot.getValue(Product.class));
                            }
                        }

                        RecyclerView rec = findViewById(R.id.rec_cart);
                        CartAdapter adapter = new CartAdapter(productList, cartList);
                        rec.setLayoutManager(new LinearLayoutManager(CartActivity.this));
                        rec.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }

//    public Map<Integer, String> getColorListOfProduct(Cart cart){
//        Map<Integer, String> colorList = new HashMap<>();
//
//        reference = database.getReference(getString(R.string.tbl_product_name));
//        reference.orderByChild("id").equalTo(cart.getProductId())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()){
//                            for(DataSnapshot proSnapshot: snapshot.getChildren()){
//                                for(DataSnapshot variantSnapshot: proSnapshot.getChildren()){
//
//                                    //check if product has color
//                                    if(variantSnapshot.hasChild("listColor")){
//                                        Integer colorId = variantSnapshot.child("listColor").
//                                                child("id").getValue(Integer.class);
//                                        String colorName = variantSnapshot.child("listColor")
//                                                .child("name").getValue(String.class);
//                                        colorList.put(colorId, colorName);
//
//                                    }
//                                }
//
//                            }
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//        return colorList;
//    }
//
//    public Map<Integer, String> getSizeListOfProduct(Cart cart){
//        Map<Integer, String> sizeList = new HashMap<>();
//
//        reference = database.getReference(getString(R.string.tbl_product_name));
//        reference.orderByChild("id").equalTo(cart.getProductId())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.exists()){
//                            for(DataSnapshot proSnapshot: snapshot.getChildren()){
//                                for(DataSnapshot variantSnapshot: proSnapshot.getChildren()){
//
//                                    //check if product has size
//                                    if(variantSnapshot.hasChild("size")){
//                                        Integer colorId = variantSnapshot.child("size").
//                                                child("id").getValue(Integer.class);
//                                        String colorName = variantSnapshot.child("size")
//                                                .child("name").getValue(String.class);
//                                        sizeList.put(colorId, colorName);
//
//                                    }
//                                }
//
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//        return sizeList;
//    }




}