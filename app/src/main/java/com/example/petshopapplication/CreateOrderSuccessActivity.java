package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.petshopapplication.API.GoshipAPI;
import com.example.petshopapplication.API.RetrofitClient;
import com.example.petshopapplication.API_model.ShipmentSearchResponse;
import com.example.petshopapplication.databinding.ActivityCreateOrderSuccessBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrderSuccessActivity extends AppCompatActivity {
    private ActivityCreateOrderSuccessBinding binding;
    private String TAG = "CreateOrderSuccessActivity";
    private String successOrderId;
    private String AUTH_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCreateOrderSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ẩn nội dung của layout cho đến khi dữ liệu API được tải hoàn toàn
        binding.getRoot().setVisibility(View.GONE);

        AUTH_TOKEN = "Bearer " + getResources().getString(R.string.goship_api_token);

        successOrderId = getIntent().getStringExtra("successOrderId");
        Log.d(TAG, "Success Order ID: " + successOrderId);

        binding.tvShipmentCode.setText(successOrderId);

        searchShipment(successOrderId);

        binding.btnConfirm.setOnClickListener(v -> {
            Intent intent = new Intent(CreateOrderSuccessActivity.this, ListOrderManageActivity.class);
            startActivity(intent);
        });

        binding.btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CreateOrderSuccessActivity.this, ListOrderManageActivity.class);
            startActivity(intent);
        });

    }


    // Phương thức gọi API searchShipment
    private void searchShipment(String code) {
        GoshipAPI api = RetrofitClient.getRetrofitInstance().create(GoshipAPI.class);

        // Gọi API với mã vận chuyển (code)
        Call<ShipmentSearchResponse> call = api.searchShipment(
                "application/json",
                "application/json",
                AUTH_TOKEN,
                code
        );

        // Xử lý phản hồi từ API
        call.enqueue(new Callback<ShipmentSearchResponse>() {
            @Override
            public void onResponse(Call<ShipmentSearchResponse> call, Response<ShipmentSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ShipmentSearchResponse shipmentResponse = response.body();
                    // Xử lý dữ liệu từ shipmentResponse ở đây
                    Log.d(TAG, "Shipment found: " + shipmentResponse.getData().get(0).getId());

                    binding.tvShipmentTitle.setText(shipmentResponse.getData().get(0).getCarrierName() + " - Shipment Code");
                    String carrierLogo = shipmentResponse.getData().get(0).getCarrierLogo();
                    Log.d(TAG, "Shipment carrierLogo: " + carrierLogo);
                    Glide.with(CreateOrderSuccessActivity.this)
                            .load(shipmentResponse.getData().get(0).getCarrierLogo())
                            .into(binding.imgShipmentLogo);
                    Log.d(TAG, "Shipment carrierLogo set binding: success");
                    ShipmentSearchResponse.Address addressFrom = shipmentResponse.getData().get(0).getAddressFrom();
                    String addFromStr = addressFrom.getWard() + " - " + addressFrom.getDistrict() + " - " + addressFrom.getCity();
                    Log.d(TAG, "Shipment addFromStr: " + addFromStr);

                    binding.addressFrom.setText(addFromStr);

                    ShipmentSearchResponse.Address addressTo = shipmentResponse.getData().get(0).getAddressTo();
                    String addtoStr = addressTo.getWard() + " - " + addressTo.getDistrict() + " - " + addressTo.getCity();
                    binding.addressTo.setText(addtoStr);

                    String shipmentStatus;
                    binding.orderStatus.setText("DANG LƯU TRỮ");


                    // Hiển thị giao diện sau khi dữ liệu đã tải xong
                    binding.getRoot().setVisibility(View.VISIBLE);


                    // Thực hiện các hành động khác với dữ liệu từ shipmentResponse nếu cần
                } else {
                    Log.e(TAG, "Không tìm thấy thông tin vận chuyển, mã lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ShipmentSearchResponse> call, Throwable t) {
                Log.e(TAG, "Lỗi khi gọi API: " + t.getMessage());
            }
        });
    }

}