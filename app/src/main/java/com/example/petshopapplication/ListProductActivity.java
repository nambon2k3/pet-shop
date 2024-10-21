package com.example.petshopapplication;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.ListProductAdapter;
import com.example.petshopapplication.databinding.ActivityListProductBinding;
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

public class ListProductActivity extends AppCompatActivity {

    ActivityListProductBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;
    private RecyclerView.Adapter productAdapter;
    private String searchText;
    private String categoryId;
    private boolean isSeaching;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityListProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        //getIntentExtra();

        initListProduct();

    }

    private void initListProduct() {
        reference = database.getReference(getString(R.string.tbl_product_name));
        //Display progress bar
        binding.prgListProduct.setVisibility(View.VISIBLE);

        List<Product> productItems = new ArrayList<>();
        Query query;

//        if(isSeaching) {
//            query  = reference.orderByChild("createdAt").startAt(searchText).endAt(searchText + '\uf8ff');
//        } else if(categoryId != null && !categoryId.isBlank()) {
//            query = reference.orderByChild("categoryId").equalTo(categoryId);
//        } else {
            query = reference.orderByChild("isDeleted").equalTo(false);
        //}
        query.addListenerForSingleValueEvent(new ValueEventListener() {
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


    public void fetchProductDetails(List<Product> productItems) {
        reference = database.getReference(getString(R.string.tbl_product_detail_name));

        List<ProductDetail> productDetailItems = new ArrayList<>();

        for(Product product : productItems) {
            Query query = reference.orderByChild("productId").equalTo(product.getId());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            productDetailItems.add(dataSnapshot.getValue(ProductDetail.class));
                        }
                        fetchCategory(productItems, productDetailItems);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    public void fetchCategory(List<Product> productItems, List<ProductDetail> productDetailItems) {
        reference = database.getReference(getString(R.string.tbl_category_name));
        List<Category> categoryItems = new ArrayList<>();

        for (Product product : productItems) {
            Query query = reference.orderByChild("id").equalTo(product.getCategoryId());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            categoryItems.add(dataSnapshot.getValue(Category.class));
                        }
                        if(!categoryItems.isEmpty()) {
                            binding.rcvListProduct.setLayoutManager(new GridLayoutManager(ListProductActivity.this, 2));
                            productAdapter = new ListProductAdapter(productItems, productDetailItems, categoryItems);
                            binding.rcvListProduct.setAdapter(productAdapter);

                        }
                        binding.prgListProduct.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }


    private void getIntentExtra() {
        categoryId = getIntent().getStringExtra("categoryId");
        searchText = getIntent().getStringExtra("searchText");

        isSeaching = getIntent().getBooleanExtra("isSearch", false);

        binding.tvLpTitle.setText(categoryName);
        binding.imvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}