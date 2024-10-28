package com.example.petshopapplication.Adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Order;
import com.example.petshopapplication.model.OrderDetail;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {

    private List<Order> orderList;
    private List<OrderDetail> orderDetailList;

    // Constructor nhận danh sách các đơn hàng và chi tiết đơn hàng
    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
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
//        OrderDetail orderDetail = null;
//        for (OrderDetail detail : orderDetailList) {
//            if (detail.getOrderId().equals(order.getId())) {
//                orderDetail = detail;
//                break;
//            }
//        }

//        if (orderDetail != null) {
//        }
        //Check length of product name
//        if(product.getName().length() > 40) {
//            holder.txt_product_name.setText(product.getName().substring(0, 30) + "...");
//        } else {
//            holder.txt_product_name.setText(product.getName());
//        }

        holder.txt_product_name.setText("Toys");
        holder.txt_product_detail.setText("High-quality dry dog food for all breeds.");
        holder.txt_price.setText(String.format("$%.2f", 20.0));
        holder.tv_old_price.setText(String.format("$%.2f", 50.66));
        holder.tv_old_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.txt_quantity.setText("x" + 10);
        holder.txt_status.setText("Processing");
        Glide.with(holder.itemView.getContext()).load("https://firebasestorage.googleapis.com/v0/b/pet-shop-4a349.appspot.com/o/pet-food-1.jpg?alt=media&token=badd23c5-9108-45f5-af2e-82a6556f8629").into(holder.imv_product_image);

        // Click btn_feedback:
        holder.btn_feedback.setOnClickListener(v -> {
            // Go to Feedback activity:
            // Intent intent = new Intent(v.getContext(), FeedbackActivity.class);
            // v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderHolder extends RecyclerView.ViewHolder {

        TextView txt_product_name, tv_old_price, txt_product_detail, txt_price,
                txt_quantity, txt_status;
        ImageView imv_product_image;
        Button btn_feedback;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);

            txt_product_name = itemView.findViewById(R.id.txt_product_name);
            tv_old_price = itemView.findViewById(R.id.tv_old_price);
            txt_product_detail = itemView.findViewById(R.id.txt_product_detail);
            txt_price = itemView.findViewById(R.id.txt_price);
            txt_quantity = itemView.findViewById(R.id.txt_quantity);
            txt_status = itemView.findViewById(R.id.txt_status);
            imv_product_image = itemView.findViewById(R.id.imv_product_image);
            btn_feedback = itemView.findViewById(R.id.btn_feedback);
        }
    }
}
