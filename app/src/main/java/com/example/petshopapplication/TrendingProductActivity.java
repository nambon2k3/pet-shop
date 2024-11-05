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

public class TrendingProductActivity extends AppCompatActivity {

    Button pie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trending_product);

        pie = findViewById(R.id.pie);

        pie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pie = new Intent(TrendingProductActivity.this, PieChartActivity.class);
                startActivity(pie);
            }
        });
    }
}