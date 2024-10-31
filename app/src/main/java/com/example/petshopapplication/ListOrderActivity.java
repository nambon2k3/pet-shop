package com.example.petshopapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
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
import com.example.petshopapplication.API_model.Address;
import com.example.petshopapplication.API_model.AddressCO;
import com.example.petshopapplication.API_model.City;
import com.example.petshopapplication.API_model.CityResponse;
import com.example.petshopapplication.API_model.CreateOrderRequest;
import com.example.petshopapplication.API_model.CreateOrderResponse;
import com.example.petshopapplication.API_model.District;
import com.example.petshopapplication.API_model.DistrictResponse;
import com.example.petshopapplication.API_model.Parcel;
import com.example.petshopapplication.API_model.ParcelCO;
import com.example.petshopapplication.API_model.RateRequest;
import com.example.petshopapplication.API_model.RateResponse;
import com.example.petshopapplication.API_model.Shipment;
import com.example.petshopapplication.API_model.ShipmentCO;
import com.example.petshopapplication.API_model.Ward;
import com.example.petshopapplication.API_model.WardResponse;
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
    private String[] tabTitles;
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

        tabTitles = getResources().getStringArray(R.array.tab_order_manage_titles);

        database = FirebaseDatabase.getInstance();
        initTablayouts();
//        initOrder();


        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        if (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            // Mạng sẵn sàng cho kết nối
            Log.e(TAG, "Internet connection available.");
            Log.d(TAG, "Start load city:");

//            loadCities();

            // START - Create Order
            String rate = "MTFfMjFfMTk0OA==";
            String fromName = "New Order";
            String fromPhone = "1111111111";
            String fromStreet = "102 Thái Thịnh";
            String fromWard = "113";
            String fromDistrict = "100900";
            String fromCity = "100000";
            String toName = "Lai Dao";
            String toPhone = "0909876543";
            String toStreet = "51 Lê Đại Hành";
            String toWard = "79";
            String toDistrict = "100200";
            String toCity = "100000";
            int cod = 500000;
            int weight = 220;
            int width = 15;
            int height = 15;
            int length = 15;
            String metadata = "Hàng dễ vỡ, vui lòng nhẹ tay. (new)";

            createOrder(rate, fromName, fromPhone, fromStreet, fromWard, fromDistrict, fromCity,
                    toName, toPhone, toStreet, toWard, toDistrict, toCity, cod, weight, width, height, length, metadata);

            // END - End Order

            Log.d(TAG, "End load city:");
        } else {
            Log.e(TAG, "No internet connection available.");
        }

//        // Call the method to load cities:
//        // Call the method to load cities
//        loadCities();
//        loadDistricts("100000");
//        loadWards("100900");
//
//        // Test get rates:
//
//        String fromDistrict = "100900";
//        String fromCity = "100000";
//        String toDistrict = "100200";
//        String toCity = "100000";
//        int cod = 500000;
//        int weight = 220;
//        int width = 10;
//        int height = 15;
//        int length = 15;
//
//        loadRates(fromDistrict, fromCity, toDistrict, toCity, cod, cod, width, height, length, weight);

    }

    private void initTablayouts() {
        // Tìm kiếm TabLayout và ViewPager2 trong layout
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);

        // Tạo ViewPagerAdapter và gán cho ViewPager2
        ViewPagerOrderManageAdapter viewPagerAdapter = new ViewPagerOrderManageAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Kiểm tra để đảm bảo vị trí hợp lệ
            if (position >= 0 && position < tabTitles.length) {
                tab.setText(tabTitles[position]);
            } else {
                tab.setText("Tab " + position); // Tên mặc định nếu ngoài phạm vi
            }
        }).attach();


    }

    private void loadCities() {
        Log.d(TAG, "Start method () load city:");
        GoshipAPI api = RetrofitClient.getRetrofitInstance().create(GoshipAPI.class);
        Call<CityResponse> call = api.getCities("application/json", "application/json", AUTH_TOKEN);
        call.enqueue(new Callback<CityResponse>() {
            @Override
            public void onResponse(Call<CityResponse> call, Response<CityResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Response successful: " + response.code());
                    CityResponse cityResponse = response.body();
                    if (cityResponse != null && cityResponse.getData() != null) {
                        List<City> cities = cityResponse.getData();
                        for (int i = 0; i < cities.size(); i++) {
                            Log.d(TAG, "City - " + (i + 1) + ": " + cities.get(i).getName());
                        }
                    } else {
                        Log.e(TAG, "City response or data is null");
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CityResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed in loadCities(): " + t.getMessage());
            }
        });
        Call<CityResponse> call1 = api.getCities("application/json", "application/json", AUTH_TOKEN);

        call1.enqueue(new Callback<CityResponse>() {
            @Override
            public void onResponse(Call<CityResponse> call, Response<CityResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Response successful: " + response.code());
                    CityResponse cityResponse = response.body();
                    if (cityResponse != null && cityResponse.getData() != null) {
                        List<City> cities = cityResponse.getData();
                        for (int i = 0; i < cities.size(); i++) {
                            Log.d(TAG, "City - " + (i + 1) + ": " + cities.get(i).getName());
                        }
                    } else {
                        Log.e(TAG, "City response or data is null");
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CityResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed in loadCities(): " + t.getMessage());
            }
        });
        Log.d(TAG, "End method () load city:");

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
        Call<DistrictResponse> call1 = api.getDistricts(cityId, "application/json", "application/json", AUTH_TOKEN);
        call1.enqueue(new Callback<DistrictResponse>() {
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
        Call<DistrictResponse> call2 = api.getDistricts(cityId, "application/json", "application/json", AUTH_TOKEN);
        call2.enqueue(new Callback<DistrictResponse>() {
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

    private void loadRates(String fromDistrict, String fromCity, String toDistrict, String toCity,
                           int cod, int amount, int width, int height, int length, int weight) {

        GoshipAPI api = RetrofitClient.getRetrofitInstance().create(GoshipAPI.class);

        // Tạo thông tin địa chỉ và gói hàng với các tham số truyền vào
        Address addressFrom = new Address(fromDistrict, fromCity);
        Address addressTo = new Address(toDistrict, toCity);
        Parcel parcel = new Parcel(cod, amount, width, height, length, weight);
        Shipment shipment = new Shipment(addressFrom, addressTo, parcel);

        RateRequest rateRequest = new RateRequest(shipment);

        // Gọi API để lấy biểu phí
        Call<RateResponse> call = api.getRates("application/json", "application/json", AUTH_TOKEN, rateRequest);
        call.enqueue(new Callback<RateResponse>() {
            @Override
            public void onResponse(Call<RateResponse> call, Response<RateResponse> response) {
                if (response.isSuccessful()) {
                    RateResponse rateResponse = response.body();
                    if (rateResponse != null && rateResponse.getData() != null) {
                        for (int i = 0; i < rateResponse.getData().size(); i++) {
                            Log.d(TAG, "Carrier " + (i + 1) + ": " + rateResponse.getData().get(i).getCarrierName() +
                                    ", Fee: " + rateResponse.getData().get(i).getTotalFee());
                        }
                    } else {
                        Log.e(TAG, "No data in rate response");
                    }
                } else {
                    Log.e(TAG, "Request failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<RateResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void createOrder(String rate, String fromName, String fromPhone, String fromStreet, String fromWard,
                             String fromDistrict, String fromCity, String toName, String toPhone,
                             String toStreet, String toWard, String toDistrict, String toCity,
                             int cod, int weight, int width, int height, int length, String metadata) {
        Log.d(TAG, "Start method () create order:");

        GoshipAPI api = RetrofitClient.getRetrofitInstance().create(GoshipAPI.class);

        // Create the Address objects for sender and receiver
        AddressCO addressFrom = new AddressCO(fromName, fromPhone, fromStreet, fromWard, fromDistrict, fromCity);
        AddressCO addressTo = new AddressCO(toName, toPhone, toStreet, toWard, toDistrict, toCity);

        // Create the Parcel object with the shipment details
        ParcelCO parcel = new ParcelCO(String.valueOf(cod), String.valueOf(weight), String.valueOf(width),
                String.valueOf(height), String.valueOf(length), metadata);

        // Create the Shipment object with rate, sender, receiver, and parcel
        ShipmentCO shipment = new ShipmentCO(rate, addressFrom, addressTo, parcel);

        // Create the CreateOrderRequest object
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(shipment);

        // Make the API call to create the order
        Call<CreateOrderResponse> call = api.createOrder("application/json", "application/json", AUTH_TOKEN, createOrderRequest);
        call.enqueue(new Callback<CreateOrderResponse>() {
            @Override
            public void onResponse(Call<CreateOrderResponse> call, Response<CreateOrderResponse> response) {
                if (response.isSuccessful()) {
                    CreateOrderResponse createOrderResponse = response.body();
                    if (createOrderResponse != null) {
                        Log.d(TAG, "Order created successfully with ID: " + createOrderResponse.getId());
                        Log.d(TAG, "Tracking Number: " + createOrderResponse.getTrackingNumber());
                        Log.d(TAG, "Carrier: " + createOrderResponse.getCarrier());
                        Log.d(TAG, "Fee: " + createOrderResponse.getFee());
                    } else {
                        Log.e(TAG, "CreateOrderResponse is null");
                    }
                } else {
                    Log.d(TAG, "Response successful: " + response.code());
                    Log.e(TAG, "Request failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<CreateOrderResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed in createOrder(): " + t.getMessage());
            }
        });
        Log.d(TAG, "End method () create order:");

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