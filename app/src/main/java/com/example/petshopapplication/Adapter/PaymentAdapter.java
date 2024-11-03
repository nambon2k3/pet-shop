package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.graphics.Paint;
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
import com.example.petshopapplication.model.Color;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.Variant;

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
            if (holder.tv_item_name != null) {
                holder.tv_item_name.setText(product.getName());
            }
            if (holder.tv_item_quantity != null) {
                holder.tv_item_quantity.setText(String.valueOf(cart.getQuantity())); // Chuyển đổi số lượng thành chuỗi
            }

            // Lấy variant đã chọn của sản phẩm
            Variant selectedVariant = null;
            for (Variant variant : product.getListVariant()) {
                if (variant.getId().equals(cart.getSelectedVariantId())) {
                    selectedVariant = variant;
                    break;
                }
            }

            if (selectedVariant != null) {
                // Hiển thị giá cũ và giá mới
                double oldPrice = selectedVariant.getPrice();
                if (holder.tv_item_old_price != null) {
                    holder.tv_item_old_price.setText(String.format("%.0f$", oldPrice)); // Giá cũ
                    holder.tv_item_old_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

                }

                if (product.getDiscount() > 0) {
                    double newPrice = oldPrice * (1 - product.getDiscount() / 100.0); // Giá mới
                    if (holder.tv_item_new_price != null) {
                        holder.tv_item_new_price.setText(String.format("%.0f$", newPrice));
                    }
                } else {
                    if (holder.tv_item_new_price != null) {
                        holder.tv_item_new_price.setText(String.format("%.0f$", oldPrice)); // Hiển thị giá gốc
                    }
                }

                // Hiển thị màu sắc và kích thước đã chọn
                String selectedColor = null;
                String selectedSize = selectedVariant.getSize() != null ? selectedVariant.getSize().getName() : null;

                for (Color color : selectedVariant.getListColor()) {
                    if (color.getId().equals(cart.getSelectedColorId())) {
                        selectedColor = color.getName();
                        break;
                    }
                }

                StringBuilder itemTypeBuilder = new StringBuilder();
                if (selectedColor != null) {
                    itemTypeBuilder.append(selectedColor);
                }
                if (selectedSize != null) {
                    if (itemTypeBuilder.length() > 0) {
                        itemTypeBuilder.append(", ");
                    }
                    itemTypeBuilder.append(selectedSize);
                }

                if (holder.tv_item_type != null) {
                    holder.tv_item_type.setText(itemTypeBuilder.toString()); // Hiển thị màu sắc và kích thước
                }

                Glide.with(context)
                        .load(product.getBaseImageURL())
                        .into(holder.imv_item);
            }
        }
    }

    public Product getProductById(String productId) {
        for (Product product : productList) {
            if (product.getId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return selectedCartItems.size();
    }

    public static class PaymentHolder extends RecyclerView.ViewHolder {
        ImageView imv_item;
        TextView tv_item_name, tv_item_old_price, tv_item_new_price, tv_item_quantity, tv_item_type;

        public PaymentHolder(@NonNull View itemView) {
            super(itemView);
            imv_item = itemView.findViewById(R.id.imv_item);
            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            tv_item_old_price = itemView.findViewById(R.id.tv_item_old_price);
            tv_item_new_price = itemView.findViewById(R.id.tv_item_new_price);
            tv_item_quantity = itemView.findViewById(R.id.tv_item_quantity); // Check the ID here
            tv_item_type = itemView.findViewById(R.id.tv_item_type);
        }
    }
}
