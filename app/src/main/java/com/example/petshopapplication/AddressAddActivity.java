package com.example.petshopapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petshopapplication.API.GoshipAPI;
import com.example.petshopapplication.API.RetrofitClient;
import com.example.petshopapplication.API_model.City;
import com.example.petshopapplication.API_model.CityResponse;
import com.example.petshopapplication.API_model.District;
import com.example.petshopapplication.API_model.DistrictResponse;
import com.example.petshopapplication.API_model.Ward;
import com.example.petshopapplication.API_model.WardResponse;
import com.example.petshopapplication.model.UAddress;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressAddActivity extends AppCompatActivity {
    private static final String TAG = "AddAddressActivity";
    private String AUTH_TOKEN;

    private String selectedCityId;
    private String selectedDistrictId;
    private int selectedWardId;

    private TextView citySelectButton;
    private TextView districtSelectButton;
    private TextView wardSelectButton;
    private EditText fullNameEditText;
    private EditText phoneEditText;

    // Khai báo biến addressesRef
    private DatabaseReference addressesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_add);

        // Khởi tạo Firebase Database và reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        addressesRef = database.getReference("addresses");

        AUTH_TOKEN = "Bearer " + getResources().getString(R.string.goship_api_token);

        // Find views by ID
        citySelectButton = findViewById(R.id.citySelectButton);
        districtSelectButton = findViewById(R.id.districtSelectButton);
        wardSelectButton = findViewById(R.id.wardSelectButton);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);

        Button completeButton = findViewById(R.id.completeButton);

        // Set button click listeners
        citySelectButton.setOnClickListener(v -> loadCities());
        districtSelectButton.setOnClickListener(v -> loadDistricts(selectedCityId));
        wardSelectButton.setOnClickListener(v -> loadWards(selectedDistrictId));

        completeButton.setOnClickListener(v -> {
            Log.d(TAG, "Complete button clicked");
            saveAddress();
        });

    }

    private void loadCities() {
        GoshipAPI api = RetrofitClient.getRetrofitInstance().create(GoshipAPI.class);
        Call<CityResponse> call = api.getCities("application/json", "application/json", AUTH_TOKEN);

        call.enqueue(new Callback<CityResponse>() {
            @Override
            public void onResponse(Call<CityResponse> call, Response<CityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<City> cities = response.body().getData();
                    displayCitySelectionDialog(cities);
                } else {
                    Log.e(TAG, "Failed to load cities: " + response.message());
                    Toast.makeText(AddressAddActivity.this, "Lỗi khi tải danh sách thành phố", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CityResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed: " + t.getMessage());
                Toast.makeText(AddressAddActivity.this, "Lỗi khi gọi API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayCitySelectionDialog(List<City> cities) {
        String[] cityNames = new String[cities.size()];
        for (int i = 0; i < cities.size(); i++) {
            cityNames[i] = cities.get(i).getName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn Thành phố")
                .setItems(cityNames, (dialog, which) -> {
                    selectedCityId = cities.get(which).getId();
                    citySelectButton.setText(cityNames[which]);
                })
                .show();
    }

    private void loadDistricts(String cityId) {
        if (cityId == null) {
            Toast.makeText(this, "Vui lòng chọn thành phố trước", Toast.LENGTH_SHORT).show();
            return;
        }

        GoshipAPI api = RetrofitClient.getRetrofitInstance().create(GoshipAPI.class);
        Call<DistrictResponse> call = api.getDistricts(cityId, "application/json", "application/json", AUTH_TOKEN);

        call.enqueue(new Callback<DistrictResponse>() {
            @Override
            public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<District> districts = response.body().getData();
                    displayDistrictSelectionDialog(districts);
                } else {
                    Log.e(TAG, "Failed to load districts: " + response.message());
                    Toast.makeText(AddressAddActivity.this, "Lỗi khi tải danh sách quận", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DistrictResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed: " + t.getMessage());
                Toast.makeText(AddressAddActivity.this, "Lỗi khi gọi API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDistrictSelectionDialog(List<District> districts) {
        String[] districtNames = new String[districts.size()];
        for (int i = 0; i < districts.size(); i++) {
            districtNames[i] = districts.get(i).getName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn Quận")
                .setItems(districtNames, (dialog, which) -> {
                    selectedDistrictId = districts.get(which).getId();
                    districtSelectButton.setText(districtNames[which]);
                })
                .show();
    }

    private void loadWards(String districtId) {
        if (districtId == null) {
            Toast.makeText(this, "Vui lòng chọn quận trước", Toast.LENGTH_SHORT).show();
            return;
        }

        GoshipAPI api = RetrofitClient.getRetrofitInstance().create(GoshipAPI.class);
        Call<WardResponse> call = api.getWards(districtId, "application/json", "application/json", AUTH_TOKEN);

        call.enqueue(new Callback<WardResponse>() {
            @Override
            public void onResponse(Call<WardResponse> call, Response<WardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ward> wards = response.body().getData();
                    displayWardSelectionDialog(wards);
                } else {
                    Log.e(TAG, "Failed to load wards: " + response.message());
                    Toast.makeText(AddressAddActivity.this, "Lỗi khi tải danh sách phường", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WardResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed: " + t.getMessage());
                Toast.makeText(AddressAddActivity.this, "Lỗi khi gọi API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayWardSelectionDialog(List<Ward> wards) {
        String[] wardNames = new String[wards.size()];
        for (int i = 0; i < wards.size(); i++) {
            wardNames[i] = wards.get(i).getName();
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn Phường")
                .setItems(wardNames, (dialog, which) -> {
                    selectedWardId = wards.get(which).getId();
                    wardSelectButton.setText(wardNames[which]);
                })
                .show();
    }

    private void saveAddress() {
        Log.d(TAG, "saveAddress() called");
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty() || selectedCityId == null || selectedDistrictId == null || selectedWardId == 0) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        String addressId = UUID.randomUUID().toString(); // Tạo ID ngẫu nhiên

        UAddress UAddress = new UAddress(
                addressId,                                   // ID địa chỉ
                fullName,                                   // Họ và tên
                phone,                                      // Số điện thoại
                citySelectButton.getText().toString(),     // Tên thành phố
                selectedCityId,                             // ID thành phố
                districtSelectButton.getText().toString(),  // Tên quận
                selectedDistrictId,                         // ID quận
                wardSelectButton.getText().toString(),      // Tên phường
                false,                                      // isDefault (ví dụ: false cho địa chỉ không mặc định)
                "u1"                                        // ID người dùng
        );

        // Lưu địa chỉ vào Firebase
        addressesRef.child(addressId).setValue(UAddress)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Địa chỉ đã được lưu!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // Trả kết quả thành công về AddressSelectionActivity
                        finish(); // Kết thúc Activity
                    } else {
                        Log.e(TAG, "Failed to save address: " + task.getException().getMessage());
                        Toast.makeText(this, "Lỗi khi lưu địa chỉ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
