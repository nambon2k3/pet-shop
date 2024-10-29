package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Cart;
import com.example.petshopapplication.model.Product;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentHolder> {
    private List<Cart> selectedCartItems; // Danh sách sản phẩm đã chọn
    private List<Product> productList; // Danh sách tất cả sản phẩm
    private Context context;

    public PaymentAdapter(List<Product> productList, List<Cart> selectedCartItems, Context context) {
        this.productList = productList;
        this.selectedCartItems = selectedCartItems;
        this.context = context; // Gán context từ activity
    }

    @NonNull
    @Override
    public PaymentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new PaymentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentHolder holder, int position) {
        Cart cart = selectedCartItems.get(position);
        Product product = getProductById(cart.getProductId());

        if (product != null) {
            // Hiển thị tên sản phẩm
            holder.tv_item_name.setText(product.getName());
            holder.tv_item_quantity.setText(String.valueOf(cart.getQuatity())); // Chuyển đổi số lượng thành chuỗi

            // Hiển thị giá cũ và giá mới
            double oldPrice = product.getListVariant().get(0).getPrice();
            if (product.getDiscount() > 0) {
                holder.tv_item_new_price.setText(String.format("%.1f$", oldPrice * (1 - product.getDiscount() / 100.0))); // Giá mới
            } else {
                holder.tv_item_new_price.setText(String.format("%.1f$", oldPrice)); // Hiển thị giá gốc
            }

            // Tải hình ảnh sản phẩm
            loadProductImage(holder.imv_item, product.getBaseImageURL());
        }
    }

    private Product getProductById(String productId) {
        for (Product product : productList) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return null; // Không tìm thấy sản phẩm
    }

    private void loadProductImage(ImageView imageView, String imageUrl) {
        if (context != null) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(imageView);
        } else {
            Log.e("PaymentAdapter", "Context is null");
        }
    }

    @Override
    public int getItemCount() {
        return selectedCartItems.size();
    }

    public static class PaymentHolder extends RecyclerView.ViewHolder {
        ImageView imv_item;
        TextView tv_item_name, tv_item_old_price, tv_item_new_price, tv_item_quantity;

        public PaymentHolder(@NonNull View itemView) {
            super(itemView);
            imv_item = itemView.findViewById(R.id.imv_item);
            tv_item_name = itemView.findViewById(R.id.tv_item_name);tv_item_old_price = itemView.findViewById(R.id.tv_item_old_price);
            tv_item_new_price = itemView.findViewById(R.id.tv_item_new_price);
            tv_item_quantity = itemView.findViewById(R.id.tv_item_quatity);
        }
    }
}
