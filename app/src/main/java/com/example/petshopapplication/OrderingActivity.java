package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrderingActivity extends AppCompatActivity {
    Button continueShoppingButton;
    Button viewOrdersButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ordering);
        continueShoppingButton = findViewById(R.id.button_continue_shopping);
        continueShoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để chuyển hướng đến HomeActivity
                Intent intent = new Intent(OrderingActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Kết thúc OrderConfirmationActivity nếu không cần quay lại
            }
        });

        viewOrdersButton = findViewById(R.id.button_view_orders);
        viewOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để chuyển hướng đến HomeActivity
                Intent intent = new Intent(OrderingActivity.this, ListOrderActivity.class);
                startActivity(intent);
                finish(); // Kết thúc OrderConfirmationActivity nếu không cần quay lại
            }
        });
    }
}