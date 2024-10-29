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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Cart;
import com.example.petshopapplication.model.Color;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.Size;
import com.example.petshopapplication.model.Variant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    private static final String CART = "Cart";
    List<Cart> cartList;
    List<Product> productList;
    Context context;

    public CartAdapter(List<Product> productList, List<Cart> cartList) {
        this.productList = productList;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartAdapter.CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartHolder holder, int position) {
        List<Color> colorList = new ArrayList<>();
        List<Color> colorList2 = new ArrayList<>();
        Map<Size, List<Color>> sizeListMap = new HashMap<>();
        List<Size> sizeList = new ArrayList<>();
        Size size = new Size();
        String selectedColor = "", selectedSize = "", item_type = "";

        Cart cart = cartList.get(position);
        Product product = getProductById(cart.getProductId());
        Double oldPrice = product.getListVariant().get(0).getPrice();

        //Get list size of product
        for (Variant variant: product.getListVariant()){
            sizeList.add(variant.getSize());
        }

        //Get product size has been selected
        for (Size s : sizeList){
            if(s.getId().equals(cart.getSelectedSizeId())){
                selectedSize = s.getName();
                size = s;
            }
        }

        //Get list color of product base on size
        for (Variant variant: product.getListVariant()){
            sizeListMap.put(variant.getSize(), variant.getListColor());
        }

        //Get product color has been selected
        colorList = sizeListMap.get(size);
        for (Color color : colorList){
            if(color.getId().equals(cart.getSelectedColorId())){
                selectedColor = color.getName();
            }
        }



        //Check if product has color and size
        if(selectedColor == null && size == null){
            holder.tv_item_type.setVisibility(View.GONE);
        } else if (selectedColor != null) {
            item_type += selectedColor;
            if(size != null){
                item_type += ", " + selectedSize;
            }
        }

        holder.tv_item_name.setText(product.getName());
        holder.tv_item_type.setText(item_type);

        //check if product is discounted
        if(product.getDiscount() > 0) {
            holder.tv_item_old_price.setText(String.format("%.1f$", oldPrice));
            holder.tv_item_old_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tv_item_new_price.setText(String.format("%.1f$", oldPrice * (1 - product.getDiscount()/100.0)));

        } else {
            holder.tv_item_old_price.setVisibility(View.GONE);
            holder.tv_item_new_price.setText(String.format("%.1f$", oldPrice));
        }
        holder.tv_item_quatity.setText(String.valueOf(cart.getQuantity()));
        Glide.with(context)
                .load(product.getBaseImageURL())
                .into(holder.imv_item);


    }


    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class CartHolder extends RecyclerView.ViewHolder {
        TextView tv_item_name, tv_item_type, tv_item_old_price, tv_item_new_price, tv_item_quatity;
        ImageView imv_item;
        CheckBox checkBox;

        public CartHolder(@NonNull View itemView) {
            super(itemView);
            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            tv_item_type = itemView.findViewById(R.id.tv_item_type);
            tv_item_old_price = itemView.findViewById(R.id.tv_item_old_price);
            tv_item_new_price = itemView.findViewById(R.id.tv_item_new_price);
            tv_item_quatity = itemView.findViewById(R.id.tv_item_quatity);
            imv_item = itemView.findViewById(R.id.imv_item);
            checkBox = itemView.findViewById(R.id.checkBox);


        }
    }

    public Product getProductById(String productId){
        for(Product product: productList){
            if (product.getId().equals(productId)){
                return product;
            }
        }
        return null;
    }
}

