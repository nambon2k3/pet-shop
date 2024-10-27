package com.example.petshopapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.API.GoshipAPI;
import com.example.petshopapplication.API.RetrofitClient;
import com.example.petshopapplication.API_model.City;
import com.example.petshopapplication.API_model.CityResponse;
import com.example.petshopapplication.API_model.District;
import com.example.petshopapplication.API_model.DistrictResponse;
import com.example.petshopapplication.API_model.Ward;
import com.example.petshopapplication.API_model.WardResponse;
import com.example.petshopapplication.Adapter.OrderAdapter;
import com.example.petshopapplication.Adapter.ProductAdapter;
import com.example.petshopapplication.databinding.ActivityHomeBinding;
import com.example.petshopapplication.databinding.ActivityListOrderBinding;
import com.example.petshopapplication.model.Order;
import com.example.petshopapplication.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListOrderActivity extends AppCompatActivity {
    ActivityListOrderBinding binding;
    FirebaseDatabase database;
    DatabaseReference reference;
    RecyclerView.Adapter orderAdapter;

    // START - Test API
    private String TAG = "CityActivity";
    private String AUTH_TOKEN;
    // END - Test API


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_order);

        AUTH_TOKEN = "Bearer " + getResources().getString(R.string.goship_api_token);
        binding = ActivityListOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        initOrder();

        // Call the method to load cities
        loadCities();
        loadDistricts("100000");
        loadWards("100900");

    }

    private void loadCities() {
        GoshipAPI api = RetrofitClient.getRetrofitInstance().create(GoshipAPI.class);

        Call<CityResponse> call = api.getCities("application/json", "application/json", AUTH_TOKEN);
        call.enqueue(new Callback<CityResponse>() {
            @Override
            public void onResponse(Call<CityResponse> call, Response<CityResponse> response) {
                if (response.isSuccessful()) {
                    CityResponse cityResponse = response.body();
                    List<City> cities = cityResponse != null ? cityResponse.getData() : new ArrayList<>();
                    int i = 1;
                    for (City city : cities) {
                        Log.d(TAG, "City - " + i + ": " + city.getName());
                        i++;
                    }
                } else {
                    Log.e(TAG, "Request Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CityResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed: " + t.getMessage());
            }
        });
    }

    private void loadDistricts(String cityId) {
        GoshipAPI api = RetrofitClient.getRetrofitInstance().create(GoshipAPI.class);

        Call<DistrictResponse> call = api.getDistricts(cityId, "application/json", "application/json", AUTH_TOKEN);
        call.enqueue(new Callback<DistrictResponse>() {
            @Override
            public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                if (response.isSuccessful()) {
                    DistrictResponse districtResponse = response.body();
                    List<District> districts = districtResponse != null ? districtResponse.getData() : new ArrayList<>();
                    int i = 1;
                    for (District district : districts) {
                        Log.d(TAG, "District - " + i + ": " + district.getName());
                        i++;
                    }
                } else {
                    Log.e(TAG, "Request Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<DistrictResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed: " + t.getMessage());
            }
        });
    }

    private void loadWards(String districtId) {
        GoshipAPI api = RetrofitClient.getRetrofitInstance().create(GoshipAPI.class);

        Call<WardResponse> call = api.getWards(districtId, "application/json", "application/json", AUTH_TOKEN);
        call.enqueue(new Callback<WardResponse>() {
            @Override
            public void onResponse(Call<WardResponse> call, Response<WardResponse> response) {
                if (response.isSuccessful()) {
                    WardResponse wardResponse = response.body();
                    List<Ward> wards = wardResponse != null ? wardResponse.getData() : new ArrayList<>();
                    int i = 1;
                    for (Ward ward : wards) {
                        Log.d(TAG, "Ward - " + i + ": " + ward.getName());
                        i++;
                    }
                } else {
                    Log.e(TAG, "Request Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WardResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed: " + t.getMessage());
            }
        });
    }



    private void initOrder() {
        reference = database.getReference(getString(R.string.tbl_order_name));

        //Display progress bar
        binding.prgListOrder.setVisibility(View.VISIBLE);

        List<Order> orderItems = new ArrayList<>();
//        orderAdapter = new OrderAdapter(productItems, categoryItems);
//        binding.rcvNewProduct.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
//        binding.rcvNewProduct.setAdapter(productAdapter);
//        binding.prgHomeNewProduct.setVisibility(View.INVISIBLE);
    }
}