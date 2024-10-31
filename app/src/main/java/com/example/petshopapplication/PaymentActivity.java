package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.API.GoshipAPI;
import com.example.petshopapplication.API.RetrofitClient;
import com.example.petshopapplication.API_model.Parcel;
import com.example.petshopapplication.API_model.Rate;
import com.example.petshopapplication.API_model.RateRequest;
import com.example.petshopapplication.API_model.RateResponse;
import com.example.petshopapplication.API_model.Shipment;
import com.example.petshopapplication.Adapter.PaymentAdapter;
import com.example.petshopapplication.API_model.Address;
import com.example.petshopapplication.Adapter.RateAdapter;
import com.example.petshopapplication.model.ShippingMethod;
import com.example.petshopapplication.model.UAddress;
import com.example.petshopapplication.model.Cart;
import com.example.petshopapplication.model.Product;
import com.example.petshopapplication.model.Variant;
import com.example.petshopapplication.model.Dimension;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements RateAdapter.OnRateSelectListener {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "PaymentActivity";
    private RecyclerView recyclerView;
    private List<Cart> selectedCartItems;
    private TextView tvTotalPrice;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private TextView addressTextView;
    private Button changeAddressButton;
    private String AUTH_TOKEN;
    private double totalAmount;
    private List<ShippingMethod> shippingMethods = new ArrayList<>();
    private int totalWidth, totalHeight, totalLength, totalWeight;
    private UAddress selectedUAddress;
    private RateAdapter rateAdapter;
    private List<Rate> rateList = new ArrayList<>();
    private TextView priceReal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        selectedCartItems = (ArrayList<Cart>) getIntent().getSerializableExtra("selectedItems");
        AUTH_TOKEN = "Bearer " + getResources().getString(R.string.goship_api_token);
        database = FirebaseDatabase.getInstance();
        tvTotalPrice = findViewById(R.id.totalPriceTextView);
        addressTextView = findViewById(R.id.addressTextView);
        changeAddressButton = findViewById(R.id.changeAddressButton);
        priceReal = findViewById(R.id.price_in_real);
        recyclerView = findViewById(R.id.recyclerViewProductList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter cho danh sách phí vận chuyển
        rateAdapter = new RateAdapter(rateList, this, this);
        RecyclerView rateRecyclerView = findViewById(R.id.rateRecyclerView);
        rateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rateRecyclerView.setAdapter(rateAdapter);

        getDefaultAddress();
        loadProductDetails();

        changeAddressButton.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, AddressSelectionActivity.class);
            intent.putExtra("selectedAddress", selectedUAddress);
            startActivityForResult(intent, REQUEST_CODE);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedUAddress = (UAddress) data.getSerializableExtra("selectedAddress");
            if (selectedUAddress != null) {
                displayAddress(selectedUAddress);
                // Gọi lại loadRates nếu cần thiết
                if (selectedUAddress.getDistrictId() != null && selectedUAddress.getCityId() != null) {
                    loadRates(selectedUAddress.getDistrictId(), selectedUAddress.getCityId(), 1, (int) totalAmount, totalWidth, totalHeight, totalLength, totalWeight);
                }
            }
        }
    }


    private void loadProductDetails() {
         List<Product> productList = new ArrayList<>();

        reference = database.getReference(getString(R.string.tbl_product_name));
        List<String> productIds = new ArrayList<>();
        for (Cart cart : selectedCartItems) {
            productIds.add(cart.getProductId());
        }

        reference.orderByChild("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    totalAmount = 0;
                    totalWidth = totalHeight = totalLength = totalWeight = 0;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String productId = dataSnapshot.child("id").getValue(String.class);
                        if (productIds.contains(productId)) {
                            Product product = dataSnapshot.getValue(Product.class);
                            if (product != null) {
                                productList.add(product);
                                for (Variant variant : product.getListVariant()) {
                                    Dimension dimension = variant.getDimension();
                                    for (Cart cart : selectedCartItems) {
                                        if (cart.getProductId().equals(productId)) {
                                            int quantity = cart.getQuantity();
                                            totalAmount += variant.getPrice() * (1 - product.getDiscount() / 100.0) * quantity;
                                            totalWidth += dimension.getWidth() * quantity;
                                            totalHeight += dimension.getHeight() * quantity;
                                            totalLength += dimension.getLength() * quantity;
                                            totalWeight += dimension.getWeight() * quantity;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    priceReal.setText("" + totalAmount);
                    PaymentAdapter paymentAdapter = new PaymentAdapter(productList, selectedCartItems, PaymentActivity.this);
                    recyclerView.setAdapter(paymentAdapter);

                    if (selectedUAddress != null) {
                        loadRates(selectedUAddress.getDistrictId(), selectedUAddress.getCityId(), 1, (int) totalAmount, totalWidth, totalHeight, totalLength, totalWeight);
                    } else {
                        Toast.makeText(PaymentActivity.this, "Địa chỉ chưa được chọn", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PaymentActivity.this, "Có lỗi xảy ra khi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRates(String toDistrict, String toCity, int cod, int amount, int width, int height, int length, int weight) {
        GoshipAPI api = RetrofitClient.getRetrofitInstance().create(GoshipAPI.class);
        String fromCityId = getString(R.string.petshop_address_city_id);
        String fromDistrictId = getString(R.string.petshop_address_district_id);
        Address addressFrom = new Address(fromDistrictId, fromCityId);
        Address addressTo = new Address(toDistrict, toCity);
        Parcel parcel = new Parcel(cod, amount, width, height, length, weight);
        Shipment shipment = new Shipment(addressFrom, addressTo, parcel);
        RateRequest rateRequest = new RateRequest(shipment);

        Log.d(TAG, "Requesting rates with: " +
                "\nFrom District: " + fromDistrictId +
                "\nFrom City: " + fromCityId +
                "\nTo District: " + toDistrict +
                "\nTo City: " + toCity +
                "\nAmount: " + amount +
                "\nWidth: " + width +
                "\nHeight: " + height +
                "\nLength: " + length +
                "\nWeight: " + weight);

        Call<RateResponse> call = api.getRates("application/json", "application/json", AUTH_TOKEN, rateRequest);
        call.enqueue(new Callback<RateResponse>() {
            @Override
            public void onResponse(Call<RateResponse> call, Response<RateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Rate> rates = response.body().getData();
                    rateAdapter.setRateList(rates);
                    for (Rate rate : rates) {
                        Log.d(TAG, "Rate ID: " + rate.getId() + ", Rate Value: " + rate.getCarrierName());
                    }
                } else {
                    Log.e(TAG, "Request failed: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to read error body: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RateResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void getDefaultAddress() {
        DatabaseReference addressReference = database.getReference("addresses");
        String userId = "u1"; // Thay đổi với ID người dùng thực tế

        addressReference.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                            Boolean isDefault = addressSnapshot.child("default").getValue(Boolean.class);
                            if (isDefault != null && isDefault) {
                                selectedUAddress = addressSnapshot.getValue(UAddress.class);
                                displayAddress(selectedUAddress);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Address", "Lỗi truy cập cơ sở dữ liệu: " + error.getMessage());
                    }
                });
    }

    private void displayAddress(UAddress address) {
        addressTextView.setText(address.getFullName() + " | " + address.getPhone() + "\n" + address.getWard() + ", " + address.getDistrict() + ", " + address.getCity());
    }



    @Override
    public void onRateSelected(double fee, boolean isSelected) {

    }
}
