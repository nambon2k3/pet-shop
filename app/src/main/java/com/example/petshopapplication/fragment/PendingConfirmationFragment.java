package com.example.petshopapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.OrderAdapter;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PendingConfirmationFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private List<Order> filteredOrderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_confirmation, container, false);

        // Tìm RecyclerView và Spinner trong layout
        recyclerView = view.findViewById(R.id.recycler_view);
        Spinner shippingUnitSpinner = view.findViewById(R.id.spinner_shipping_unit);

        // Thiết lập dữ liệu mẫu cho danh sách đơn hàng
        orderList = new ArrayList<>();

// Tạo dữ liệu mẫu cho đơn hàng
        orderList.add(Order.builder()
                .id("123")
                .userId("user001")
                .orderDate(Calendar.getInstance().getTime())  // Ngày hiện tại
                .amount(328900)
                .status("Giao Hàng Nhanh")  // Đơn vị vận chuyển
                .build());

        orderList.add(Order.builder()
                .id("124")
                .userId("user002")
                .orderDate(Calendar.getInstance().getTime())
                .amount(442400)
                .status("Giao Hàng Nhanh")
                .build());
        orderList.add(Order.builder()
                .id("123")
                .userId("user001")
                .orderDate(Calendar.getInstance().getTime())  // Ngày hiện tại
                .amount(328900)
                .status("Giao Hàng Nhanh")  // Đơn vị vận chuyển
                .build());

        orderList.add(Order.builder()
                .id("124")
                .userId("user002")
                .orderDate(Calendar.getInstance().getTime())
                .amount(442400)
                .status("Giao Hàng Nhanh")
                .build());
        orderList.add(Order.builder()
                .id("123")
                .userId("user001")
                .orderDate(Calendar.getInstance().getTime())  // Ngày hiện tại
                .amount(328900)
                .status("Giao Hàng Nhanh")  // Đơn vị vận chuyển
                .build());

        orderList.add(Order.builder()
                .id("124")
                .userId("user002")
                .orderDate(Calendar.getInstance().getTime())
                .amount(442400)
                .status("Giao Hàng Nhanh")
                .build());

        orderList.add(Order.builder()
                .id("125")
                .userId("user003")
                .orderDate(Calendar.getInstance().getTime())
                .amount(155000)
                .status("Giao Hàng Tiết kiệm")
                .build());

        orderList.add(Order.builder()
                .id("126")
                .userId("user004")
                .orderDate(Calendar.getInstance().getTime())
                .amount(200000)
                .status("Giao Hàng Nhanh")
                .build());

        // Thiết lập RecyclerView với Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        filteredOrderList = new ArrayList<>(orderList); // Khởi tạo với tất cả đơn hàng
        orderAdapter = new OrderAdapter(filteredOrderList);
        recyclerView.setAdapter(orderAdapter);

        // Thiết lập Adapter cho Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.shipping_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shippingUnitSpinner.setAdapter(adapter);

        // Xử lý sự kiện khi chọn đơn vị vận chuyển
        shippingUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedShippingUnit = parent.getItemAtPosition(position).toString();
                filterOrdersByShippingUnit(selectedShippingUnit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });

        return view;
    }

    // Lọc danh sách đơn hàng theo đơn vị vận chuyển hoặc trạng thái
    private void filterOrdersByShippingUnit(String shippingUnit) {
        filteredOrderList.clear();

        if (shippingUnit.equals("Tất cả")) {
            filteredOrderList.addAll(orderList); // Hiển thị tất cả
        } else {
            for (Order order : orderList) {
                if (order.getStatus().equals(shippingUnit)) { // Lọc theo status của đơn hàng
                    filteredOrderList.add(order);
                }
            }
        }
        orderAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu sau khi lọc
    }

}
