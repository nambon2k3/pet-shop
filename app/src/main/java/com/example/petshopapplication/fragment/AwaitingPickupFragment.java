package com.example.petshopapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.API.GoshipAPI;
import com.example.petshopapplication.API.RetrofitClient;
import com.example.petshopapplication.Adapter.ProvinceAdapter;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Province;
import com.example.petshopapplication.model.ProvinceResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AwaitingPickupFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProvinceAdapter provinceAdapter;
    private List<Province> provinceList = new ArrayList<>();
    private List<Province> filteredProvinceList = new ArrayList<>();

    Retrofit retrofit = RetrofitClient.getRetrofitInstance();
    GoshipAPI goshipAPI = retrofit.create(GoshipAPI.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending_confirmation, container, false);

        // Tìm RecyclerView và Spinner trong layout
        recyclerView = view.findViewById(R.id.recycler_view);
        Spinner shippingUnitSpinner = view.findViewById(R.id.spinner_shipping_unit);

        // Thiết lập RecyclerView với Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        provinceAdapter = new ProvinceAdapter(filteredProvinceList);
        recyclerView.setAdapter(provinceAdapter);

        // Gọi API để lấy danh sách tỉnh thành
        getProvincesFromAPI();

        // Thiết lập Adapter cho Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.shipping_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shippingUnitSpinner.setAdapter(adapter);

        // Xử lý sự kiện khi chọn đơn vị vận chuyển
        shippingUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedShippingUnit = parent.getItemAtPosition(position).toString();
                filterProvincesByShippingUnit(selectedShippingUnit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });

        return view;
    }

    private void getProvincesFromAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://sandbox.goship.io/api/v2/") // Base URL của Goship API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoshipAPI goshipAPI = retrofit.create(GoshipAPI.class);

        // Thêm token Authorization
        String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjIyMGZjNDQwNDNhZDgyNGIxOGMxODA3Y2YzY2VkODQ5MjJhOTljZTgwZTYwMDYwODc1MzdhN2JiYzkwMjU0OGUyNDI1ZjA2Yjk4YzY4NjQzIn0.eyJhdWQiOiIxMzAiLCJqdGkiOiIyMjBmYzQ0MDQzYWQ4MjRiMThjMTgwN2NmM2NlZDg0OTIyYTk5Y2U4MGU2MDA2MDg3NTM3YTdiYmM5MDI1NDhlMjQyNWYwNmI5OGM2ODY0MyIsImlhdCI6MTcyOTYxNDQzMCwibmJmIjoxNzI5NjE0NDMwLCJleHAiOjE3NjExNTA0MzAsInN1YiI6IjMzMzQiLCJzY29wZXMiOltdfQ.HsgopEvG_zGyJ7kTPpfY5Pst9uo2vY3YN9bK9s8YaKQQrRfrXzQRO0WnjTB0yzZ_bgK9srjRSuopR82dwj8sPdcSPjXPV-czZeQfwiHLVAKDj6MKqnLF_A4IQIYz8MCgcxYfd-vrdSbdzGHejcctJKEAYJx4ucHoVCU4KRWqPnbXUZysZLT2_ytuztpWlqkOPhttI0u2z1wX0ctoR0jLpaSI_o7wTkFjtnZHu_4zxackwH96FXQq-5FpdmtNsRXmij6dLP1gKqVNVcSp9Q2mkMIqnrwNfLRyYI8QzvSHUJPtwydyTib97mjo1GT1nt69nxzF3pf10K-KE0ZwIBS7qxIrM4QqcZeM_hADx8iKzOuLumMbsYSZwuiLe3oLXGcP-whDcJxEUzlDdcxPRJPav9B79o7BQFuQ_Se1mY9Vw5gV6d7ZaWh3JxaxORhiicObpKD6G9BamD0-ltRJ0EZwjAFc2uMedAcdGnSHRtN11wP2I-sysutACXTsKX7bGz370_sSkacpY1LI_X88U91n1UKxZP-vGLowaHeZdB_n_R_0IDHFqyCzEwbChOp2BmOch06aXqaij-v9O0jHCcBWxmpIX2qEBcEeI6hf6D_AiH4NQoL8HRJRsK1GU12sw2u5j3WqCzDwL7zUstHYw3BDbyIUjK4Yt0PJD6JbnU5K2KA"; // Thay bằng token thực tế
        Call<ProvinceResponse> call = goshipAPI.getProvinces(token);

        call.enqueue(new Callback<ProvinceResponse>() {

            @Override
            public void onResponse(Call<ProvinceResponse> call, Response<ProvinceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    provinceList = response.body().getData();
                    filteredProvinceList.clear();
                    filteredProvinceList.addAll(provinceList);
                    provinceAdapter.notifyDataSetChanged();

                    // Log để kiểm tra JSON
                    Log.d("API Response", "JSON Data: " + response.body().toString());
                } else {
                    Log.e("API Error", "Response Code: " + response.code());
                }
            }


            @Override
            public void onFailure(Call<ProvinceResponse> call, Throwable t) {
                Log.e("API Failure", "Error: " + t.getMessage());
                t.printStackTrace();
            }

        });
    }


    // Lọc danh sách province theo đơn vị vận chuyển hoặc trạng thái
    private void filterProvincesByShippingUnit(String shippingUnit) {
        filteredProvinceList.clear();

        if (shippingUnit.equals("Tất cả")) {
            filteredProvinceList.addAll(provinceList); // Hiển thị tất cả
        } else {
            for (Province province : provinceList) {
                if (province.getName().equals(shippingUnit)) { // Lọc theo tên của province
                    filteredProvinceList.add(province);
                }
            }
        }
        provinceAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu sau khi lọc
    }
}
