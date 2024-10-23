package com.example.petshopapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Cart;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    List<Cart> productList;
    private OnItemCheckListener onItemCheckListener;

    public CartAdapter(List<Cart> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public CartAdapter.CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartHolder holder, int position) {
        //holder.imv_item.setImageResource(productList.get(position).get());
        //holder.tv_itemName.setText(productList.get(position).getName());
        //holder.tv_itemPrice.setText(productList.get(position).getPrice());
        //holder.tv_itemQuatity.setText(productList.get(position).getQuatity());


        //CheckBox Event
        //Reset all event
//        holder.checkBox.setOnCheckedChangeListener(null);
//        holder.checkBox.setChecked(false);
//        holder.checkBox.setChecked(productList.get(position).isChecked());
//        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                    productList.get(position).setChecked(isChecked);
//                    if (isChecked) {
//                        onItemCheckListener.onItemCheck(productList.get(position));
//                    } else {
//                        onItemCheckListener.onItemUncheck(productList.get(position));
//                    }
//                }
//        );
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class CartHolder extends RecyclerView.ViewHolder {
        TextView tv_itemName, tv_itemPrice, tv_itemQuatity;
        ImageView imv_item;
        CheckBox checkBox;

        public CartHolder(@NonNull View itemView) {
            super(itemView);
            tv_itemName = itemView.findViewById(R.id.tv_itemName);
            tv_itemPrice = itemView.findViewById(R.id.tv_itemPrice);
            tv_itemQuatity = itemView.findViewById(R.id.tv_itemQuatity);
            imv_item = itemView.findViewById(R.id.imv_item);
            checkBox = itemView.findViewById(R.id.checkBox);


        }
    }
    public interface OnItemCheckListener {
        void onItemCheck(Cart item);
        void onItemUncheck(Cart item);
    }
}

