package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopapplication.CartActivity;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Cart;
import com.example.petshopapplication.model.Product;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    List<Cart> cartList;
    List<Product> productList;
    List<Boolean> selectedItems; // Danh sách trạng thái checkbox
    Context context;
    CartActivity cartActivity;

    public CartAdapter(List<Product> productList, List<Cart> cartList, CartActivity cartActivity, List<Boolean> selectedItems) {
        this.productList = productList;
        this.cartList = cartList;
        this.cartActivity = cartActivity;
        this.selectedItems = selectedItems;
        this.context = cartActivity; // Gán context từ activity
    }


    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        Cart cart = cartList.get(position);
        Product product = getProductById(cart.getProductId());

        if (product != null) {
            holder.tv_item_name.setText(product.getName());
            holder.tv_item_quatity.setText(cart.getQuatity());

            double oldPrice = product.getListVariant().get(0).getPrice();
            if (product.getDiscount() > 0) {
                holder.tv_item_old_price.setText(String.format("%.1f$", oldPrice));
                holder.tv_item_old_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tv_item_new_price.setText(String.format("%.1f$", oldPrice * (1 - product.getDiscount() / 100.0)));
            } else {
                holder.tv_item_old_price.setVisibility(View.GONE);
                holder.tv_item_new_price.setText(String.format("%.1f$", oldPrice));
            }

            // Kiểm tra context trước khi sử dụng Glide
            if (context != null) {
                Glide.with(context)
                        .load(product.getBaseImageURL())
                        .into(holder.imv_item);
            } else {
                // Xử lý lỗi context là null
                Log.e("CartAdapter", "Context is null");
            }

            holder.checkBox.setChecked(selectedItems.get(position));
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                selectedItems.set(position, isChecked);
                cartActivity.calculateTotalPrice();
            });
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

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartHolder extends RecyclerView.ViewHolder {
        ImageView imv_item;
        TextView tv_item_name, tv_item_old_price, tv_item_new_price, tv_item_quatity;
        CheckBox checkBox;

        public CartHolder(@NonNull View itemView) {
            super(itemView);
            imv_item = itemView.findViewById(R.id.imv_item);
            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            tv_item_old_price = itemView.findViewById(R.id.tv_item_old_price);
            tv_item_new_price = itemView.findViewById(R.id.tv_item_new_price);
            tv_item_quatity = itemView.findViewById(R.id.tv_item_quatity);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
