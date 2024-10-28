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
import androidx.viewpager2.widget.ViewPager2;

import com.example.petshopapplication.API.GoshipAPI;
import com.example.petshopapplication.API.RetrofitClient;
import com.example.petshopapplication.API_model.City;
import com.example.petshopapplication.API_model.CityResponse;
import com.example.petshopapplication.Adapter.OrderAdapter;
import com.example.petshopapplication.Adapter.ProductAdapter;
import com.example.petshopapplication.Adapter.ViewPagerOrderManageAdapter;
import com.example.petshopapplication.databinding.ActivityHomeBinding;
import com.example.petshopapplication.databinding.ActivityListOrderBinding;
import com.example.petshopapplication.model.Order;
import com.example.petshopapplication.model.Product;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
        initTablayouts();
//        initOrder();

        // Call the method to load cities:
        loadCities();

    }

    private void initTablayouts() {
        ViewPager2 viewPager = binding.viewPager;
        TabLayout tabLayout = binding.tabLayout;
//        binding.prgListOrder.setVisibility(View.INVISIBLE);
//        binding.rcvOrders.setVisibility(View.INVISIBLE);
        // Set Adapter for ViewPager2:
        ViewPagerOrderManageAdapter adapter = new ViewPagerOrderManageAdapter(this);
        viewPager.setAdapter(adapter);

        // Get tab titles from strings.xml:
        String[] tabTitles = getResources().getStringArray(R.array.tab_order_manage_titles);

        // Connect TabLayout with ViewPager2:
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                String[] parts = tabTitles[position].split("\\|");
                tab.setText(parts[0]);
            }
        }).attach();
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


    private void initOrder() {
//        reference = database.getReference(getString(R.string.tbl_order_name));
//        //Display progress bar
//        binding.prgListOrder.setVisibility(View.VISIBLE);
//        List<Order> orderItems = new ArrayList<>();
//        orderItems.add(new Order());
//        orderItems.add(new Order());
//        orderItems.add(new Order());
//        orderItems.add(new Order());
//        orderItems.add(new Order());
//        orderItems.add(new Order());
//        orderItems.add(new Order());
//        orderItems.add(new Order());
//        orderItems.add(new Order());
//
//        orderAdapter = new OrderAdapter(orderItems);
//        binding.rcvOrders.setLayoutManager(new LinearLayoutManager(ListOrderActivity.this, LinearLayoutManager.VERTICAL, false));
//        binding.rcvOrders.setAdapter(orderAdapter);
//        binding.prgListOrder.setVisibility(View.INVISIBLE);
    }
}