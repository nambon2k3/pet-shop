package com.example.petshopapplication;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.ListProductAdapter;
import com.example.petshopapplication.Adapter.ListProductCategoryAdapter;
import com.example.petshopapplication.databinding.ActivityListProductBinding;
import com.example.petshopapplication.model.Category;
import com.example.petshopapplication.model.Product;
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
    private RecyclerView.Adapter productAdapter, categoryAdapter;
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

        //initListProduct();
        initCategory();

    }

    private void initCategory() {
        reference = database.getReference(getString(R.string.tbl_category_name));

        List<Category> categoryItems = new ArrayList<>();
        Query query = reference.orderByChild("isDeleted").equalTo(false);
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        categoryItems.add(dataSnapshot.getValue(Category.class));
                    }

                    if(categoryItems.size() > 1) {
                        categoryAdapter = new ListProductCategoryAdapter(categoryItems);
                        binding.rcvListProductCategory.setLayoutManager(new LinearLayoutManager(ListProductActivity.this, RecyclerView.HORIZONTAL, false));
                        binding.rcvListProductCategory.setAdapter(categoryAdapter);
                    }
                    initListProduct(categoryItems);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void initListProduct(List<Category> categoryItems) {
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
                        productItems.add(dataSnapshot.getValue(Product.class));
                    }

                    productAdapter = new ListProductAdapter(productItems, categoryItems);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    private void getIntentExtra() {
        categoryId = getIntent().getStringExtra("categoryId");
        searchText = getIntent().getStringExtra("searchText");

        isSeaching = getIntent().getBooleanExtra("isSearch", false);

        //binding.tvLpTitle.setText(categoryName);
        binding.imvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}