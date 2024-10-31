package com.example.petshopapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private static final int REQUEST_CODE_ADD_ADDRESS = 1;
    private static final int REQUEST_CODE_UPDATE_ADDRESS = 2;
    private static final String TAG = "AddressSelectionActivity";

    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private List<Address> addressList;
    private DatabaseReference addressRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_manage);

        recyclerView = findViewById(R.id.address_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(addressList, this, address -> {
            Intent intent = new Intent(AddressSelectionActivity.this, AddressUpdateActivity.class);
            intent.putExtra("addressId", address.getAddressId());
            startActivityForResult(intent, REQUEST_CODE_UPDATE_ADDRESS);
        });
        recyclerView.setAdapter(addressAdapter);

        addressRef = FirebaseDatabase.getInstance().getReference("addresses");

        // Lấy tất cả địa chỉ của người dùng
        fetchUserAddresses("u1");

        LinearLayout addAddressLayout = findViewById(R.id.add_address_layout);
        addAddressLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AddressSelectionActivity.this, AddressAddActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_ADDRESS);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_ADD_ADDRESS || requestCode == REQUEST_CODE_UPDATE_ADDRESS) && resultCode == RESULT_OK) {Log.d(TAG, "onActivityResult: Triggered for requestCode " + requestCode);
            Log.d(TAG, "onActivityResult: Triggered for requestCode " + requestCode);
            fetchUserAddresses("u1");
        }
    }


    private void fetchUserAddresses(String userId) {
        Log.d(TAG, "fetchUserAddresses: Fetching addresses for userId " + userId);
        addressRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addressList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Address address = snapshot.getValue(Address.class);
                    if (address != null) {
                        addressList.add(address);
                    }
                }
                addressAdapter.notifyDataSetChanged(); // Cập nhật adapter sau khi thay đổi dữ liệu
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddressSelectionActivity.this, "Error fetching addresses: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
