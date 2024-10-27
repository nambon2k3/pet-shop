package com.example.petshopapplication;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.petshopapplication.databinding.ActivityAddProductVariantBinding;
import com.example.petshopapplication.databinding.ActivityHomeBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;




public class AddProductVariantActivity extends AppCompatActivity {

    Button btnShowAddSize;
    ActivityAddProductVariantBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product_variant);

        binding = ActivityAddProductVariantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        database = FirebaseDatabase.getInstance();

        // Set OnClickListener for the button
//        binding.addPvButtonAddSize.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAddSize();
//            }
//        });
    }

    private void showAddSize(){

    }
}