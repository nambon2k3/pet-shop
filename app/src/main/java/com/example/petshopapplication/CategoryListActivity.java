package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.CartAdapter;
import com.example.petshopapplication.Adapter.ListCategoryAdapter;
import com.example.petshopapplication.databinding.ActivityCategoryListBinding;
import com.example.petshopapplication.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CategoryListActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    List<Category> categoryList = new ArrayList<>();
    ActivityCategoryListBinding binding;
    int totalQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCategoryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_category_list);

        //Handling add category button
        binding.imvAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryListActivity.this, AddCategoryActivity.class);
                startActivity(intent);
            }
        });

        //Initialize firebase
        database = FirebaseDatabase.getInstance();

        initListCategory();
    }

    public void initListCategory() {

        reference = database.getReference(getString(R.string.tbl_category_name));

        reference.orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Category category = dataSnapshot.getValue(Category.class);
                        categoryList.add(category);
                    }
                    //totalQuantity = categoryList.size();
                    RecyclerView rec = findViewById(R.id.rcv_list_category);
                    ListCategoryAdapter adapter = new ListCategoryAdapter(categoryList, CategoryListActivity.this);
                    rec.setLayoutManager(new LinearLayoutManager(CategoryListActivity.this));
                    rec.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    public void fetchProductQuantityToCategory(List<Category> categoryList, OnQuantityFetchListener listener) {
//        Map<Category, Long> categoryIntegerMap = new LinkedHashMap<>();
//
//
//        for (Category category : categoryList) {
//            categoryIntegerMap.put(category, getProductQuantityOfCategory(category, listener));
//
//        }
//
//
//
//
//    }
//
//    public Long getProductQuantityOfCategory(Category category, OnQuantityFetchListener listener) {
//        Long quantity = 0L;
//        reference = database.getReference(getString(R.string.tbl_product_name));
//        reference.orderByChild("categoryId").equalTo(category.getId())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            quantity = snapshot.getChildrenCount();
//                            quantity = listener.onQuantityFetch(quantity);
//
//                        }
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//        return quantity;
//
//    }
//
//    public interface OnQuantityFetchListener {
//        Long onQuantityFetch(Long quantity);
//    }

}