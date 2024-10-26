package com.example.petshopapplication;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.petshopapplication.Adapter.FeedBackAdapter;
import com.example.petshopapplication.Adapter.ListProductAdapter;
import com.example.petshopapplication.Adapter.ListProductCategoryAdapter;
import com.example.petshopapplication.databinding.ActivityProductDetailBinding;
import com.example.petshopapplication.model.Category;
import com.example.petshopapplication.model.FeedBack;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class ProductDetailActivity extends AppCompatActivity {


    ActivityProductDetailBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;
    private String productId;
    private List<Product> productItems;
    List<Category> categoryItems;
    private final int ITEMS_PER_PAGE = 16;
    private ListProductAdapter productAdapter;
    private FeedBackAdapter feedBackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        binding.tvEmptyFeedback.setVisibility(View.GONE);

        getIntend();
        //initCategory();
        initProductDetail(productId);

    }


    private void initProductDetail(String productID) {
        reference = database.getReference(getString(R.string.tbl_product_name));
        Query query = reference.orderByChild("id").equalTo(productID);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Update product detail view
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                         Product product = dataSnapshot.getValue(Product.class);
                         binding.tvProductName.setText(product.getName());

                        double oldPrice = product.getBasePrice();
                        String imageUrl = product.getBaseImageURL();
                        int stock = 0;

                        //Check if product have variants
                        if(!product.getListVariant().isEmpty()) {
                            oldPrice = product.getListVariant().get(0).getPrice();
                            stock = product.getListVariant().get(0).getStock();
                            //check if product have color variants
                            if(!product.getListVariant().get(0).getListColor().isEmpty()) {
                                imageUrl = product.getListVariant().get(0).getListColor().get(0).getImageUrl();
                                stock = product.getListVariant().get(0).getListColor().get(0).getStock();
                            }
                        }

                        //check if product is discounted
                        if(product.getDiscount() > 0) {
                            binding.tvDiscount.setText(String.valueOf("-" + product.getDiscount()) + "%");
                            binding.tvOldPrice.setText(String.format("%.1f$", oldPrice));
                            binding.tvOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            binding.tvNewPrice.setText(String.format("%.1f$", oldPrice * (1 - product.getDiscount()/100.0)));
                        } else {
                            binding.tvDiscount.setVisibility(View.GONE);
                            binding.tvOldPrice.setVisibility(View.GONE);
                            binding.tvNewPrice.setText(String.format("%.1f$", oldPrice));
                        }

                        binding.tvDescription.setText(product.getDescription());
                        binding.tvStockProduct.setText("Stock: " + stock);

                         Glide.with(ProductDetailActivity.this)
                                 .load(imageUrl)
                                 .transform(new CenterCrop(), new RoundedCorners(30))
                                 .into(binding.imvProductImage);

                         initCategory(product);
                         fetchFeedback(product);

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void fetchFeedback(Product product) {
        reference = database.getReference(getString(R.string.tbl_feedback_name));

        List<FeedBack> feedbackItems = new ArrayList<>();

        Query query = reference.orderByChild("productId").equalTo(product.getId());
        query.limitToLast(2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        feedbackItems.add(dataSnapshot.getValue(FeedBack.class));
                    }
                    if(feedbackItems.size() > 0) {
                        fetchUserData(feedbackItems);
                    } else {
                        binding.tvEmptyFeedback.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void fetchUserData(List<FeedBack> feedbackItems) {
        reference = database.getReference(getString(R.string.tbl_user_name));
        List<User> userItems = new ArrayList<>();
        for(FeedBack feedBack : feedbackItems) {
            //Reference to the user table
            reference = database.getReference(getString(R.string.tbl_user_name));
            //Get user data by user Id in feed back
            Query query = reference.orderByChild("id").equalTo(feedBack.getUserId());
            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        //Get user data from database
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            userItems.add(user);
                        }
                        if(userItems.size() > 0) {
                            feedBackAdapter = new FeedBackAdapter(feedbackItems, userItems);
                            binding.rcvFeedback.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this, RecyclerView.VERTICAL, true));
                            binding.rcvFeedback.setNestedScrollingEnabled(false);
                            binding.rcvFeedback.setAdapter(feedBackAdapter);
                        }
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            });
        }
    }

    private void initCategory(Product product) {
        reference = database.getReference(getString(R.string.tbl_category_name));

        categoryItems = new ArrayList<>();
        Query query = reference.orderByChild("isDeleted");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        categoryItems.add(dataSnapshot.getValue(Category.class));
                    }
                    fetchSuggestProduct(product);

                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void fetchSuggestProduct(Product product) {
        reference = database.getReference(getString(R.string.tbl_product_name));

        productItems = new ArrayList<>();
        Query query;
        query = reference.orderByChild("categoryId").equalTo(product.getCategoryId());

        query.limitToFirst(ITEMS_PER_PAGE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        //Check status of product
                        if(!product.isDeleted()) {
                            productItems.add(dataSnapshot.getValue(Product.class));
                        }
                    }
                    productAdapter = new ListProductAdapter(productItems, categoryItems);
                    binding.rcvSuggestProduct.setLayoutManager(new GridLayoutManager(ProductDetailActivity.this, 2));
                    binding.rcvSuggestProduct.setAdapter(productAdapter);
                }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void getIntend() {
        productId = getIntent().getStringExtra("productId");
        binding.backBtn.setOnClickListener(v -> finish());
    }



}