package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopapplication.PrepareOrderActivity;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Order;
import com.example.petshopapplication.model.OrderDetail;
import com.example.petshopapplication.utils.Validate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private List<Order> orderList;
    private boolean btnRate;
    private boolean isInventory;
    private String orderStatus;
    private List<OrderDetail> orderDetailsList;
    private Context context;

    // Constructor nhận danh sách các đơn hàng
    public OrderAdapter(List<Order> orderList, boolean btnRate, String orderStatus, boolean isInventory) {
        this.orderList = orderList;
        this.btnRate = btnRate;
        this.orderStatus = orderStatus;
        this.isInventory = isInventory;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.view_holder_order_item, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        Order order = orderList.get(position);

        // Set trạng thái và tổng giá trị đơn hàng
        holder.txt_status.setText(order.getStatus());

        // Tính tổng số sản phẩm (quantity) trong order
        int totalQuantity = 0;
        List<OrderDetail> orderDetailsList = order.getOrderDetails();
        if (orderDetailsList != null && !orderDetailsList.isEmpty()) {
            for (OrderDetail detail : orderDetailsList) {
                totalQuantity += detail.getQuantity();
            }
            Log.d("OrderAdapter", "Order details size for Order ID " + order.getId() + ": " + orderDetailsList.size());

            // Thiết lập RecyclerView cho orderDetails
            OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(orderDetailsList);
            holder.rcv_order_details.setLayoutManager(new LinearLayoutManager(context));
            holder.rcv_order_details.setAdapter(orderDetailAdapter);
        } else {
            Log.d("OrderAdapter", "No order details found for Order ID " + order.getId());
        }

        // Set tổng số sản phẩm và tổng giá trị của đơn hàng
        if (totalQuantity > 1) {
            holder.txt_total_price_title.setText("x" + totalQuantity + " products");
        } else {
            holder.txt_total_price_title.setText("x" + totalQuantity + " product");
        }
        holder.txt_total_price.setText(String.format("Total: %s", Validate.formatVND(order.getTotalAmount())));

        // Hiển thị hoặc ẩn nút feedback
        if (!btnRate)
            holder.btn_feedback.setVisibility(View.GONE);

        // Kiểm tra trạng thái đơn hàng và thiết lập trạng thái nút
        if ("Shipping".equals(orderStatus)) {
            holder.txt_shipping_status.setVisibility(View.VISIBLE);
            holder.txt_shipping_status.setText("Shipping Status: In Transit");
            holder.btn_feedback.setText("Received");
        } else if ("Delivered".equals(orderStatus)) {
            holder.txt_shipping_status.setVisibility(View.GONE);
            holder.line2.setVisibility(View.GONE);
        } else {
            holder.txt_shipping_status.setVisibility(View.GONE);
            holder.line3.setVisibility(View.GONE);
            holder.line2.setVisibility(View.GONE);
        }

        // Kiểm tra trạng thái isInventory để hiển thị các nút phù hợp
        if (isInventory) {
            holder.btn_prepare_order.setVisibility(View.VISIBLE);
            holder.btn_cancel_order.setVisibility(View.VISIBLE);
            holder.btn_feedback.setVisibility(View.GONE);
            holder.line3.setVisibility(View.VISIBLE);
        } else {
            holder.btn_prepare_order.setVisibility(View.GONE);
            holder.btn_cancel_order.setVisibility(View.GONE);
        }

        holder.btn_prepare_order.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, PrepareOrderActivity.class);

            intent.putExtra("order_id", order.getId());

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderHolder extends RecyclerView.ViewHolder {
        TextView txt_status, txt_total_price, txt_total_price_title, txt_shipping_status;
        View line2, line3;
        RecyclerView rcv_order_details;
        Button btn_feedback, btn_prepare_order, btn_cancel_order;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            txt_status = itemView.findViewById(R.id.txt_status);
            line2 = itemView.findViewById(R.id.line2);
            line3 = itemView.findViewById(R.id.line3);
            txt_total_price = itemView.findViewById(R.id.txt_total_price);
            txt_total_price_title = itemView.findViewById(R.id.txt_total_price_title);
            txt_shipping_status = itemView.findViewById(R.id.txt_shipping_status);
            rcv_order_details = itemView.findViewById(R.id.rcv_order_details);
            btn_feedback = itemView.findViewById(R.id.btn_feedback);
            btn_prepare_order = itemView.findViewById(R.id.btn_prepare_order);
            btn_cancel_order = itemView.findViewById(R.id.btn_cancel_order);
        }
    }
}
