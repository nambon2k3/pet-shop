package com.example.petshopapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.Adapter.AddressAdapter;
import com.example.petshopapplication.model.Address;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddressSelectionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter; // Bạn cần tạo AddressAdapter
    private List<Address> addressList;
    private DatabaseReference addressRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_manage);

        recyclerView = findViewById(R.id.address_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(addressList, this); // Tạo adapter cho RecyclerView

        recyclerView.setAdapter(addressAdapter);

        addressRef = FirebaseDatabase.getInstance().getReference("addresses"); // Tham chiếu đến địa chỉ

        // Lấy tất cả địa chỉ của người dùng
        fetchUserAddresses("user123"); // Thay đổi user ID nếu cần
    }

    private void fetchUserAddresses(String userId) {
        addressRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(AddressSelectionActivity.this, "Không có dữ liệu địa chỉ", Toast.LENGTH_SHORT).show();
                    return;
                }

                addressList.clear(); // Xóa danh sách địa chỉ cũ
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Address address = snapshot.getValue(Address.class);
                    if (address != null) {
                        addressList.add(address); // Thêm địa chỉ vào danh sách
                        Log.d("AddressSelection", "Địa chỉ: " + address.getFullName());
                    }
                }
                addressAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddressSelectionActivity.this, "Error fetching addresses: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
