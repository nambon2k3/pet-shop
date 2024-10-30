package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Cart;
import com.example.petshopapplication.model.Color;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.Size;
import com.example.petshopapplication.model.Variant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    private static final String CART = "Cart";
    List<Cart> cartList;
    List<Product> productList;
    Context context;
    FirebaseDatabase database;
    DatabaseReference reference;
    OnCartItemCheckedListener listener;


    public CartAdapter(List<Product> productList, List<Cart> cartList, Context context, OnCartItemCheckedListener listener) {
        this.productList = productList;
        this.cartList = cartList;
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public CartAdapter.CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_cart, parent, false);
        return new CartHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartHolder holder, int position) {
        List<Color> colorList = new ArrayList<>();
        Map<Size, List<Color>> sizeListMap = new HashMap<>();
        List<Size> sizeList = new ArrayList<>();
        List<Variant> variantList = new ArrayList<>();
        Size size = new Size();
        String selectedColor = "", selectedSize = "", item_type = "";
        Double oldPrice = 0.;
        int stock = 0;
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        Cart cart = cartList.get(position);
        Product product = getProductById(cart.getProductId());
        variantList = product.getListVariant();

        //Get price, size has been selected for product
        for (Variant variant: variantList){
            if(cart.getSelectedVariantId().equals(variant.getId())){
                oldPrice = variant.getPrice();
                selectedSize = variant.getSize().getName();
                colorList = variant.getListColor();
            }
        }

        //Get list size of product
        //(use for user chose size again)
        for (Variant variant: variantList){
            sizeList.add(variant.getSize());
        }

        //Get list color of product base on size
        for (Variant variant: variantList){
            sizeListMap.put(variant.getSize(), variant.getListColor());
        }

        //Get product color has been selected
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

            holder.tv_item_old_price.setText(currencyFormatter.format(oldPrice));
            holder.tv_item_old_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tv_item_new_price.setText(currencyFormatter.format( oldPrice * (1 - product.getDiscount()/100.0)));

        } else {
            holder.tv_item_old_price.setVisibility(View.GONE);
            holder.tv_item_new_price.setText(currencyFormatter.format(oldPrice));
        }

            holder.tv_item_quatity.setText(String.valueOf(cart.getQuantity()));
        Glide.with(context)
                .load(product.getBaseImageURL())
                .into(holder.imv_item);


        //Get product stock of product has been selected
        for (Color color : colorList){
            if(selectedColor.equals(color.getName())){
                stock = color.getStock();
            }
        }

        //Handler the event of quantity button
            holder.btn_increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = cart.getQuantity();


                    quantity++;
                    updateQuantityToDb(quantity, cart.getCartId());
                }

            });



        holder.btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = cart.getQuantity();
                quantity--;

                //If quantity equals 1 user can not decrease
                if(quantity >= 1){
                    updateQuantityToDb(quantity, cart.getCartId());
                }
            }
        });

        //Handle the event checkbox of cart item
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                //Update for cart item is checked or is unchecked
                cart.setChecked(b);

                //Inform to Activity about the changed of checkbox
                //=> Calculate again the total of bill
                listener.onCartItemCheckedChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class CartHolder extends RecyclerView.ViewHolder {
        TextView tv_item_name, tv_item_type, tv_item_old_price, tv_item_new_price, tv_item_quatity;
        ImageView imv_item, btn_increase, btn_decrease;
        CheckBox checkBox;

        public CartHolder(@NonNull View itemView) {
            super(itemView);
            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            tv_item_type = itemView.findViewById(R.id.tv_item_type);
            tv_item_old_price = itemView.findViewById(R.id.tv_item_old_price);
            tv_item_new_price = itemView.findViewById(R.id.tv_item_new_price);
            tv_item_quatity = itemView.findViewById(R.id.tv_item_quatity);
            imv_item = itemView.findViewById(R.id.imv_item);
            btn_decrease = itemView.findViewById(R.id.btn_decrease);
            btn_increase = itemView.findViewById(R.id.btn_increase);
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

    //Update quantity on layout
    private void updateQuantityInLayout(String cartId, int newQuantity) {
        for (Cart item : cartList) {
            if (item.getCartId().equals(cartId)) {
                item.setQuantity(newQuantity);
                notifyDataSetChanged();
                break;
            }
        }
        //Inform to Activity about the changed of checkbox
        //=> Calculate again the total of bill
        listener.onCartItemCheckedChanged();
    }

    //Update quantity in firebase
    public void updateQuantityToDb(int quantity, String cartId){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(context.getString(R.string.tbl_cart_name));

        reference.orderByChild("cartId").equalTo(cartId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot cartSnapshot : snapshot.getChildren()) {

                        //Update new quantity for cart item
                        cartSnapshot.getRef().child("quantity").setValue(quantity)
                                .addOnSuccessListener(aVoid ->{
                                            Log.d("Firebase", "Quantity updated successfully.");
                                            updateQuantityInLayout(cartId, quantity);
                                        }
                                        )
                                .addOnFailureListener(e -> Log.e("Firebase", "Failed to update quantity", e));
                    }
                } else {
                    Log.e("Firebase", "Cart with cartId " + cartId + " not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Interface communicate between Adapter and Activity
    //when checkbox of one cart item is changed
    public interface OnCartItemCheckedListener{
        void onCartItemCheckedChanged();
    }
}

