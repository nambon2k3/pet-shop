package com.example.petshopapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.PaymentAdapter;
import com.example.petshopapplication.model.Cart;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment); // Thay thế bằng layout của bạn

        // Nhận danh sách sản phẩm đã chọn từ intent
        List<Cart> selectedItems = (ArrayList<Cart>) getIntent().getSerializableExtra("selectedItems");

        // Hiển thị danh sách sản phẩm trong layout thanh toán
        RecyclerView recyclerView = findViewById(R.id.recyclerViewProductList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (selectedItems != null && !selectedItems.isEmpty()) {
            // Tạo adapter và thiết lập dữ liệu
            PaymentAdapter paymentAdapter = new PaymentAdapter(selectedItems);
            recyclerView.setAdapter(paymentAdapter);
        } else {
            Toast.makeText(this, "Không có sản phẩm nào được chọn!", Toast.LENGTH_SHORT).show();
        }
    }
}
