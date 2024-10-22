package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Cart;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Cart> cartItems;
    private Context context;
    private TextView totalPriceView; // Reference to total price TextView

    public CartAdapter(List<Cart> cartItems, Context context, TextView totalPriceView) {
        this.cartItems = cartItems;
        this.context = context;
        this.totalPriceView = totalPriceView;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public List<Cart> getSelectedItems() {
        List<Cart> selectedItems = new ArrayList<>();
        for (Cart item : cartItems) {
            if (item.isDeleted()) { // Check if the item is selected
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imvItem;
        TextView tvItemName;
        TextView tvItemPrice;
        CheckBox checkBox;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imvItem = itemView.findViewById(R.id.imv_item);
            tvItemName = itemView.findViewById(R.id.tv_itemName);
            tvItemPrice = itemView.findViewById(R.id.tv_itemPrice);
            checkBox = itemView.findViewById(R.id.checkBox);

            // Set up checkbox listener
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Cart cartItem = cartItems.get(getAdapterPosition());
                cartItem.setDeleted(isChecked); // Update item status
                updateTotalPrice(); // Recalculate total price whenever checkbox state changes
            });
        }

        public void bind(Cart cartItem) {
            tvItemName.setText(cartItem.getProductName());
            tvItemPrice.setText("₫" + cartItem.getPrice());
            Glide.with(imvItem.getContext()).load(cartItem.getImageUrl()).into(imvItem);
            checkBox.setChecked(cartItem.isDeleted()); // Update checkbox status
        }

        private void updateTotalPrice() {
            double totalPrice = 0.0;
            for (Cart item : cartItems) {
                if (item.isDeleted()) {
                    totalPrice += item.getPrice(); // Sum selected item prices
                }
            }
            totalPriceView.setText("₫" + totalPrice); // Update total price TextView
        }
    }
}
