package com.example.petshopapplication;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.petshopapplication.Adapter.ColorAdapter;
import com.example.petshopapplication.Adapter.FeedBackAdapter;
import com.example.petshopapplication.Adapter.ListProductAdapter;
import com.example.petshopapplication.Adapter.ListProductCategoryAdapter;
import com.example.petshopapplication.Adapter.ProductImageAdapter;
import com.example.petshopapplication.Adapter.SizeAdapter;
import com.example.petshopapplication.databinding.ActivityProductDetailBinding;
import com.example.petshopapplication.model.Category;
import com.example.petshopapplication.model.Color;
import com.example.petshopapplication.model.FeedBack;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.Size;
import com.example.petshopapplication.model.User;
import com.example.petshopapplication.model.Variant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class    ProductDetailActivity extends AppCompatActivity implements ProductImageAdapter.OnProductImageClickListener, SizeAdapter.OnSizeClickEventListener, ColorAdapter.OnColorClickEventListener{


    ActivityProductDetailBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;
    private String productId;
    private List<Product> productItems;
    private List<Category> categoryItems;
    private List<Color> colorItems;
    private List<Variant> variantItems;
    private List<Size> sizeItems;
    private final int ITEMS_PER_PAGE = 16;
    private ListProductAdapter productAdapter;
    private FeedBackAdapter feedBackAdapter;
    private ProductImageAdapter productImageAdapter;
    private ColorAdapter colorAdapter;
    private SizeAdapter sizeAdapter;
    ConstraintLayout layout;
    Dialog dialog;

    RecyclerView colorCartRecyclerView, sizeCartRecyclerView;


    //pop up view
    ImageView imv_cart_product;
    TextView tv_cart_new_price, tv_cart_old_price, tv_cart_stock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        colorItems = new ArrayList<>();
        sizeItems = new ArrayList<>();
        variantItems = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        binding.tvEmptyFeedback.setVisibility(View.GONE);
        layout = binding.productDetails;

        //create dialog of pop up for add to cart
        dialog = new Dialog(ProductDetailActivity.this);
        dialog.setContentView(R.layout.pop_up_add_cart);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);







        getIntend();
        initProductDetail(productId);
        binding.btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add product to cart
                dialog.show();
            }
        });

    }

    private void initProductDetail(String productID) {
        reference = database.getReference(getString(R.string.tbl_product_name));
        Query query = reference.orderByChild("id").equalTo(productID);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Update product detail view
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                         Product product = dataSnapshot.getValue(Product.class);
                         binding.tvProductName.setText(product.getName());

                        //Check if product have variants
                        if(!product.getListVariant().isEmpty()) {
                            Variant variant = product.getListVariant().get(0);
                            if(!variant.getListColor().isEmpty()) {
                                Color color = variant.getListColor().get(0);
                                fillProductData(product, variant, color);
                            } else {
                                fillProductData(product, variant, null);
                            }
                        } else {
                            fillProductData(product, null, null);
                        }



                         initCategory(product);
                         fetchFeedback(product);
                         fetchProductImage(product);
                         fillCartVariantInformation(product);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fillCartVariantInformation(Product product) {
        if(!product.getListVariant().isEmpty()) {
            //file size information into dialog
            variantItems.addAll(product.getListVariant());
            for (Variant variant : product.getListVariant()) {
                sizeItems.add(variant.getSize());
            }
            sizeAdapter = new SizeAdapter(sizeItems, ProductDetailActivity.this);
            sizeCartRecyclerView = dialog.findViewById(R.id.rcv_size);
            sizeCartRecyclerView.setLayoutManager(new GridLayoutManager(ProductDetailActivity.this, 2));
            sizeCartRecyclerView.setAdapter(sizeAdapter);

            //fill color information into dialog
            Variant variant = product.getListVariant().get(0);
            if(!variant.getListColor().isEmpty()) {
                colorItems.addAll(variant.getListColor());
                colorAdapter = new ColorAdapter(colorItems,this::onColorClick);
                colorCartRecyclerView = dialog.findViewById(R.id.rcv_color);
                colorCartRecyclerView.setLayoutManager(new GridLayoutManager(ProductDetailActivity.this, 1));
                colorCartRecyclerView.setAdapter(colorAdapter);
            }
        }
    }

    private void fillProductData(Product product, Variant variant, Color color) {

        //fill product information into dialog
        imv_cart_product = dialog.findViewById(R.id.imv_cart_product_image);
        tv_cart_new_price = dialog.findViewById(R.id.tv_cart_new_price);
        tv_cart_old_price = dialog.findViewById(R.id.tv_cart_old_price);
        tv_cart_stock = dialog.findViewById(R.id.tv_cart_stock);

        binding.tvProductName.setText(product.getName());
        double oldPrice = product.getBasePrice();
        String imageUrl = product.getBaseImageURL();
        int stock = 0;

        //Check if product have variants
        if(variant != null) {
            oldPrice = variant.getPrice();
            stock = variant.getStock();

            //check if product have color variants
            if(color != null) {
                imageUrl = color.getImageUrl();
                stock = color.getStock();
            }
        }




        //check if product is discounted
        if(product.getDiscount() > 0) {
            //fill cart information
            tv_cart_old_price.setText(String.format("%.1f$", oldPrice));
            tv_cart_old_price.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tv_cart_new_price.setText(String.format("%.1f$", oldPrice * (1 - product.getDiscount()/100.0)));
            //fill product information
            binding.tvDiscount.setText(String.valueOf("-" + product.getDiscount()) + "%");
            binding.tvOldPrice.setText(String.format("%.1f$", oldPrice));
            binding.tvOldPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvNewPrice.setText(String.format("%.1f$", oldPrice * (1 - product.getDiscount()/100.0)));
        } else {
            //fill cart information
            tv_cart_old_price.setVisibility(View.GONE);
            tv_cart_new_price.setText(String.format("%.1f$", oldPrice));
            //fill product information
            binding.tvDiscount.setVisibility(View.GONE);
            binding.tvOldPrice.setVisibility(View.GONE);
            binding.tvNewPrice.setText(String.format("%.1f$", oldPrice));
        }

        binding.tvDescription.setText(product.getDescription());
        binding.tvStockProduct.setText("Stock: " + stock);

        //fill stock cart
        tv_cart_stock.setText("Stock: " + stock);

        //Product image preview
        Glide.with(ProductDetailActivity.this)
                .load(imageUrl)
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(binding.imvProductImage);

        //Get Image into product cart image
        Glide.with(ProductDetailActivity.this)
                .load(imageUrl)
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(imv_cart_product);
    }


    public void fetchProductImage(Product product) {
        productImageAdapter = new ProductImageAdapter(product, ProductDetailActivity.this);
        binding.rcvImageProduct.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
        binding.rcvImageProduct.setAdapter(productImageAdapter);
    }



    private void fetchFeedback(Product product) {
        reference = database.getReference(getString(R.string.tbl_feedback_name));

        List<FeedBack> feedbackItems = new ArrayList<>();

        Query query = reference.orderByChild("productId").equalTo(product.getId());
        query.limitToLast(2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        feedbackItems.add(dataSnapshot.getValue(FeedBack.class));
                    }
                    if(feedbackItems.size() > 0) {
                        fetchUserData(feedbackItems);
                    } else {
                        binding.tvEmptyFeedback.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void fetchUserData(List<FeedBack> feedbackItems) {
        reference = database.getReference(getString(R.string.tbl_user_name));
        List<User> userItems = new ArrayList<>();
        for(FeedBack feedBack : feedbackItems) {
            //Reference to the user table
            reference = database.getReference(getString(R.string.tbl_user_name));
            //Get user data by user Id in feed back
            Query query = reference.orderByChild("id").equalTo(feedBack.getUserId());
            query.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        //Get user data from database
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            userItems.add(user);
                        }
                        if(userItems.size() > 0) {
                            feedBackAdapter = new FeedBackAdapter(feedbackItems, userItems);
                            binding.rcvFeedback.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this, RecyclerView.VERTICAL, true));
                            binding.rcvFeedback.setNestedScrollingEnabled(false);
                            binding.rcvFeedback.setAdapter(feedBackAdapter);
                        }
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            });
        }
    }

    private void initCategory(Product product) {
        reference = database.getReference(getString(R.string.tbl_category_name));

        categoryItems = new ArrayList<>();
        Query query = reference.orderByChild("isDeleted");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        categoryItems.add(dataSnapshot.getValue(Category.class));
                    }
                    fetchSuggestProduct(product);

                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void fetchSuggestProduct(Product product) {
        reference = database.getReference(getString(R.string.tbl_product_name));

        productItems = new ArrayList<>();
        Query query;
        query = reference.orderByChild("categoryId").equalTo(product.getCategoryId());

        query.limitToFirst(ITEMS_PER_PAGE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        //Check status of product
                        if(!product.isDeleted()) {
                            productItems.add(dataSnapshot.getValue(Product.class));
                        }
                    }
                    productAdapter = new ListProductAdapter(productItems, categoryItems);
                    binding.rcvSuggestProduct.setLayoutManager(new GridLayoutManager(ProductDetailActivity.this, 2));
                    binding.rcvSuggestProduct.setAdapter(productAdapter);
                }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void getIntend() {
        productId = getIntent().getStringExtra("productId");
        binding.backBtn.setOnClickListener(v -> finish());
    }


    @Override
    public void onProductImageClick(Product product, Variant variant, Color color) {
        fillProductData(product, variant, color);
    }

    @Override
    public void onSizeClickEvent(Size size) {
        Variant variant = getVariantBySize(size.getId());
        colorItems.clear();
        colorItems.addAll(variant.getListColor());
        colorAdapter.notifyDataSetChanged();
    }


    private void fillProductCartData(Size size, Color color) {

    }




    private Variant getVariantBySize(String sizeId) {
        for (Variant variant : variantItems) {
            if(variant.getSize().getId().equals(sizeId)) {
                return variant;
            }
        }
        return null;
    }

    @Override
    public void onColorClick(Color color) {

    }
}