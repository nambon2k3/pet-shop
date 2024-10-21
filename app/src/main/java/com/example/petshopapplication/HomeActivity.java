package com.example.petshopapplication;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.CategoryAdapter;
import com.example.petshopapplication.Adapter.ProductAdapter;
import com.example.petshopapplication.databinding.ActivityHomeBinding;
import com.example.petshopapplication.model.Category;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.ProductDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;
    RecyclerView.Adapter productAdapter, categoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        database = FirebaseDatabase.getInstance();
        initNewProduct();
        //initProductDetail();
        initCategory();

    }

    private void initNewProduct(){
        reference = database.getReference(getString(R.string.tbl_product_name));
        //Display progress bar
        binding.prgHomeNewProduct.setVisibility(View.VISIBLE);

        List<Product> productItems = new ArrayList<>();
        Query query = reference.orderByChild("createdAt");
        query.limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        if(!product.isDeleted()) {
                            productItems.add(product);
                        }
                    }
                    fetchProductDetails(productItems);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void initProductDetail(){
        reference = database.getReference("productdetails");
        //Display progress bar
        binding.prgHomeNewProduct.setVisibility(View.VISIBLE);

        List<ProductDetail> productDetailItems = new ArrayList<>();
        Query query = reference.orderByChild("discount");
        query.limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        //ProductDetail productDetail = dataSnapshot.getValue(ProductDetail.class);
                        ProductDetail productDetail = new ProductDetail();
                        productDetail.setProductDetailId(dataSnapshot.getKey());
                        productDetail.setPrice(dataSnapshot.child("price").getValue(Double.class));
                        productDetailItems.add(productDetail);
                    }
                    if(productDetailItems.size() > 0) {
                        // Update product details in product adapter
                        binding.rcvNewProduct.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        productAdapter = new ProductAdapter(null, productDetailItems);
                        binding.rcvNewProduct.setAdapter(productAdapter);

                    }
                    binding.prgHomeNewProduct.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void fetchProductDetails(List<Product> product) {
        reference = database.getReference(getString(R.string.tbl_product_detail_name));
        //Display progress bar
        binding.prgHomeNewProduct.setVisibility(View.VISIBLE);

        List<ProductDetail> productDetailItems = new ArrayList<>();

        for(Product p : product) {
            Query query = reference.orderByChild("productId").equalTo(p.getId());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            productDetailItems.add(dataSnapshot.getValue(ProductDetail.class));
                        }
                        if(productDetailItems.size() > 0) {
                            // Update product details in product adapter
                            binding.rcvNewProduct.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            productAdapter = new ProductAdapter(product, productDetailItems);
                            binding.rcvNewProduct.setAdapter(productAdapter);

                        }
                        binding.prgHomeNewProduct.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }





    private void initCategory() {
        reference = database.getReference(getString(R.string.tbl_role_category));
        //Display progress bar
        binding.prgHomeCategory.setVisibility(View.VISIBLE);

        List<Category> categoryItems = new ArrayList<>();
        Query query = reference.orderByChild("isDeleted").equalTo(false);
        query.limitToFirst(6).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        categoryItems.add(dataSnapshot.getValue(Category.class));
                    }

                    if(categoryItems.size() > 1) {
                        categoryAdapter = new CategoryAdapter(categoryItems);
                        binding.rcvHomeCategory.setLayoutManager(new GridLayoutManager(HomeActivity.this, 3));
                        binding.rcvHomeCategory.setAdapter(categoryAdapter);
                    }
                    binding.prgHomeCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}