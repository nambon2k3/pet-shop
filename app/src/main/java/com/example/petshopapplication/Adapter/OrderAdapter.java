package com.example.petshopapplication.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.petshopapplication.AddFeedbackActivity;
import com.example.petshopapplication.PrepareOrderActivity;
import com.example.petshopapplication.R;
import com.example.petshopapplication.ViewDetailOrderActivity;
import com.example.petshopapplication.ViewFeedBackItemActivity;
import com.example.petshopapplication.model.FeedBack;
import com.example.petshopapplication.model.Order;
import com.example.petshopapplication.model.OrderDetail;
import com.example.petshopapplication.utils.Validate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderHolder> {
    private String TAG = "OrderAdapter";
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

        // Gọi phương thức loadPaymentAmount để lấy và hiển thị tổng giá trị thanh toán
        loadPaymentAmount(order.getPaymentId(), holder.txt_total_price);
//        holder.txt_total_price.setText(String.format("Total: %s", Validate.formatVND(order.getTotalAmount())));

        // Hiển thị hoặc ẩn nút feedback
        if (!btnRate)
            holder.btn_feedback.setVisibility(View.GONE);

        // Kiểm tra trạng thái đơn hàng và thiết lập trạng thái nút
        if ("Shipping".equals(orderStatus) || orderStatus.equals("Delivered")) {
            holder.txt_shipping_status.setVisibility(View.VISIBLE);
            holder.txt_shipping_status.setText("Shipping Status: In Transit");

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

            if (orderStatus.equals("Canceled") || orderStatus.equals("Shipping") || orderStatus.equals("Delivered")) {
                holder.btn_cancel_order.setVisibility(View.GONE);
                holder.btn_prepare_order.setVisibility(View.GONE);
                holder.btn_view_order_detail.setVisibility(View.VISIBLE);
                if (orderStatus.equals("Shipping") || orderStatus.equals("Delivered")) {
                    holder.txt_shipping_status.setVisibility(View.VISIBLE);
                    holder.txt_shipping_status.setText("Shipping Status: In Transit");

                    if (orderStatus.equals("Delivered")) {
                        holder.line2.setVisibility(View.VISIBLE);
                    }
                }
            } else if (orderStatus.equals("all")) {
                if (order.getStatus().equals("Canceled")) {
                    holder.btn_cancel_order.setVisibility(View.GONE);
                    holder.btn_prepare_order.setVisibility(View.GONE);
                    holder.btn_view_order_detail.setVisibility(View.VISIBLE);
                } else if (order.getStatus().equals("Shipping")) {
                    holder.btn_cancel_order.setVisibility(View.GONE);
                    holder.btn_prepare_order.setVisibility(View.GONE);
                    holder.btn_view_order_detail.setVisibility(View.VISIBLE);
                    holder.txt_shipping_status.setVisibility(View.VISIBLE);
                    holder.line2.setVisibility(View.VISIBLE);
                    holder.txt_shipping_status.setText("Shipping Status: In Transit");
                } else if (order.getStatus().equals("Delivered")) {
                    holder.btn_cancel_order.setVisibility(View.GONE);
                    holder.btn_prepare_order.setVisibility(View.GONE);
                    holder.btn_view_order_detail.setVisibility(View.VISIBLE);
                    holder.txt_shipping_status.setVisibility(View.VISIBLE);
                    holder.txt_shipping_status.setText("Shipping Status: In Transit");
                    holder.line2.setVisibility(View.VISIBLE);
                } else {
                    holder.btn_view_order_detail.setVisibility(View.GONE);
                }
            } else {
                holder.btn_view_order_detail.setVisibility(View.GONE);
            }
            // User
        } else {
            holder.btn_prepare_order.setVisibility(View.GONE);
            holder.btn_cancel_order.setVisibility(View.GONE);
            holder.btn_view_order_detail.setVisibility(View.VISIBLE);
            holder.line2.setVisibility(View.VISIBLE);
            if (orderStatus.equals("Delivered")) {
                holder.btn_feedback.setVisibility(View.VISIBLE);
            } else if ("Shipping".equals(orderStatus)) {
                // Check if status = Shipment Completed (*****)
                holder.btn_confirm_received.setVisibility(View.VISIBLE);
            }
        }

        holder.btn_prepare_order.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, PrepareOrderActivity.class);

            intent.putExtra("order_id", order.getId());

            context.startActivity(intent);
        });

        holder.btn_view_order_detail.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, ViewDetailOrderActivity.class);

            intent.putExtra("order_id", order.getId());

            context.startActivity(intent);
        });

        holder.btn_cancel_order.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Cancel Order")
                    .setMessage("Are you sure you want to cancel this order?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Cancel order
                        cancelOrder(order.getId());
                        Log.d(TAG, "Cancel Order ID: " + order.getId() + "| Order Total: " + order.getTotalAmount());
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        holder.btn_confirm_received.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm received order")
                    .setMessage("Are you sure you received this order?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Cancel order
//                        cancelOrder(order.getId());
                        Log.d(TAG, "Delivered Order ID: " + order.getId() + "| Order Total: " + order.getTotalAmount());
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        checkFeedbackStatus(order.getUserId(), order.getId(), holder.btn_feedback);

        // Hide feedback button if btnRate is false
        if (!btnRate) {
            holder.btn_feedback.setVisibility(View.GONE);
        }
    }

    private void cancelOrder(String orderId) {
        // Set the status of the order to "Canceled" in Firebase
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("orders").child(orderId);
        orderRef.child("status").setValue("Canceled").addOnSuccessListener(aVoid -> {
            // Successfully updated status in Firebase
            Log.d(TAG, "Order canceled successfully.");

            // Remove the order from the local list
//            orderList.removeIf(order -> order.getId().equals(orderId));

            // Notify the adapter to update the RecyclerView
            notifyDataSetChanged();

        }).addOnFailureListener(e -> {
            // Show an error message if the update fails
            Log.d(TAG, "Failed to cancel order: " + e.getMessage());
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
        Button btn_feedback, btn_prepare_order, btn_cancel_order, btn_view_order_detail, btn_confirm_received;

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
            btn_confirm_received = itemView.findViewById(R.id.btn_confirm_received);
            btn_prepare_order = itemView.findViewById(R.id.btn_prepare_order);
            btn_cancel_order = itemView.findViewById(R.id.btn_cancel_order);
            btn_view_order_detail = itemView.findViewById(R.id.btn_view_order_detail);
        }
    }

    private void loadPaymentAmount(String paymentId, TextView txtTotalPrice) {
        DatabaseReference paymentReference = FirebaseDatabase.getInstance().getReference("payments").child(paymentId);
        paymentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double totalAmount = snapshot.child("amount").getValue(Double.class);
                    if (totalAmount != null) {
                        txtTotalPrice.setText(String.format("Total: %s", Validate.formatVND(totalAmount)));
                    } else {
                        Log.e(TAG, "Total amount is null for paymentId: " + paymentId);
                    }
                } else {
                    Log.e(TAG, "No payment data found for paymentId: " + paymentId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load payment amount: " + error.getMessage());
            }
        });
    }

    private void checkFeedbackStatus(String userId, String orderId, Button feedbackButton) {
        System.out.println("order id ---- " + orderId);
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance()
                .getReference(context.getString(R.string.tbl_feedback_name));

        Query query = feedbackRef.orderByChild("orderId").equalTo(orderId);
        query.addValueEventListener(new ValueEventListener() {

            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FeedBack feedback = dataSnapshot.getValue(FeedBack.class);
                    if (snapshot.exists() && !feedback.isDeleted()) {
                        feedbackButton.setBackgroundColor(Color.parseColor(context.getString(R.color.button_background)));
                        feedbackButton.setText("View Feedback");
                        feedbackButton.setOnClickListener(v -> {
                            Intent intent = new Intent(context, ViewFeedBackItemActivity.class);
                            intent.putExtra("orderId", orderId);
                            intent.putExtra("userId", userId);
                            context.startActivity(intent);
                        });
                    } else {
                        feedbackButton.setBackgroundColor(Color.RED);
                        feedbackButton.setText("Rate");
                        feedbackButton.setOnClickListener(v -> {
                            Intent intent = new Intent(context, AddFeedbackActivity.class);
                            intent.putExtra("orderId", orderId);
                            intent.putExtra("userId", userId);
                            context.startActivity(intent);
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi truy vấn nếu cần
            }
        });
    }
}
