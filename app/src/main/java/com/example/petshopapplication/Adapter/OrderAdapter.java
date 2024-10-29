package com.example.petshopapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Order;
import com.example.petshopapplication.model.OrderDetail;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private List<Order> orderList;
    private List<OrderDetail> orderDetailList;

    // Constructor nhận danh sách các đơn hàng và chi tiết đơn hàng
    public OrderAdapter(List<Order> orderList, List<OrderDetail> orderDetailList) {
        this.orderList = orderList;
        this.orderDetailList = orderDetailList;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_order, parent, false); // Sử dụng layout item_order
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {

        Order order = orderList.get(position);

        // Find detail of order:
        OrderDetail orderDetail = null;
        for (OrderDetail detail : orderDetailList) {
            if (detail.getOrderId().equals(order.getId())) {
                orderDetail = detail;
                break;
            }
        }

        if (orderDetail != null) {
            // Hiển thị các thông tin trong ViewHolder
            holder.textViewStoreName.setText("");
            holder.textViewProductName.setText("Product Name");
            holder.textViewPrice.setText(String.format("₫%,.0f", orderDetail.getPrice()));
            holder.textViewQuantity.setText("x" + orderDetail.getQuantity());
            holder.textViewStatus.setText(order.getStatus());
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderHolder extends RecyclerView.ViewHolder {

        TextView textViewStoreName, textViewProductName, textViewPrice, textViewQuantity, textViewStatus;
        ImageView imvProductImage;
        Button btnFeedback;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các thành phần UI
            textViewStoreName = itemView.findViewById(R.id.tvStoreName);
            textViewProductName = itemView.findViewById(R.id.txt_product_name);
            textViewPrice = itemView.findViewById(R.id.txt_price);
            textViewQuantity = itemView.findViewById(R.id.txt_quantity);
            textViewStatus = itemView.findViewById(R.id.txt_status);
            imvProductImage = itemView.findViewById(R.id.imv_product_image);
            btnFeedback = itemView.findViewById(R.id.btn_feedback); // Nút đánh giá
        }
    }
}
