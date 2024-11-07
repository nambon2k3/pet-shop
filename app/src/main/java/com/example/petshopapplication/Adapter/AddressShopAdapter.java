package com.example.petshopapplication.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopapplication.R;
import com.example.petshopapplication.model.UAddress;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter

public class AddressShopAdapter extends RecyclerView.Adapter<AddressShopAdapter.AddressViewHolder> {
    private String TAG = "AddressShopAdapter";
    private List<UAddress> addressList;
    private Context context;
    private String selectedAddressId; // Lưu trữ ID của địa chỉ được chọn

    public AddressShopAdapter(List<UAddress> addressList) {
        this.addressList = addressList;

        // Tìm địa chỉ mặc định ban đầu khi Adapter được khởi tạo
        for (UAddress address : addressList) {
            if (address.isDefault()) {
                selectedAddressId = address.getAddressId();
                break;
            }
        }
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.view_holder_address_shop_item, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        UAddress address = addressList.get(position);
        holder.tvNamePhone.setText(address.getFullName() + " - " + address.getPhone());
        holder.tvAddress.setText(address.getWard() + " - " + address.getDistrict() + " - " + address.getCity());
        holder.tvSupport.setText("Pickup not supported");
//        holder.ivCheck.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);
        // Kiểm tra nếu đây là địa chỉ mặc định được chọn
        if (selectedAddressId == null) {
            holder.ivCheck.setVisibility(address.isDefault() ? View.VISIBLE : View.INVISIBLE);
        } else {
            holder.ivCheck.setVisibility(address.getAddressId().equals(selectedAddressId) ? View.VISIBLE : View.INVISIBLE);
        }
        holder.switchDefault.setChecked(address.isDefault() ? true : false);


        holder.switchDefault.setOnCheckedChangeListener(null); // Bỏ lắng nghe sự thay đổi trước đó
        holder.switchDefault.setChecked(address.isDefault()); // Đặt trạng thái của Switch theo thuộc tính của Address

        holder.switchDefault.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Nếu Switch được chuyển sang Checked, đặt trạng thái mặc định cho Address này
                for (UAddress addr : addressList) {
                    addr.setDefault(addr.getAddressId().equals(address.getAddressId())); // Chỉ đặt là mặc định cho Address hiện tại
                }
                notifyDataSetChanged(); // Cập nhật lại giao diện để chỉ một Switch được Checked

                // Gọi hàm cập nhật Firebase
                updateDefaultAddressOnFirebase(address);
            } else {
                address.setDefault(false); // Nếu bỏ chọn Switch, đặt mặc định thành false
            }
        });


        holder.tvDefaultTag.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);



        // Event click to select address
        holder.itemView.setOnClickListener(v -> {
            selectedAddressId = address.getAddressId(); // Cập nhật ID của địa chỉ được chọn
            notifyDataSetChanged(); // Thông báo cập nhật dữ liệu để hiển thị lại
        });

        // Event click edit address
        holder.btnEditAddress.setOnClickListener(v -> {

        });
        // Event click set Default
        holder.switchDefault.setOnClickListener(v -> {

        });


    }
    private void updateDefaultAddressOnFirebase(UAddress selectedAddress) {
        DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("addresses");

        // Tìm tất cả địa chỉ của User "Inventory" và cập nhật thuộc tính default
        addressRef.orderByChild("userId").equalTo("Inventory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UAddress address = snapshot.getValue(UAddress.class);
                    if (address != null) {
                        boolean isSelectedAddress = address.getAddressId().equals(selectedAddress.getAddressId());
                        snapshot.getRef().child("default").setValue(isSelectedAddress);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Error updating default address");
            }
        });
    }
    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamePhone, tvAddress, tvSupport, tvDefaultTag, btnEditAddress;
        ImageView ivCheck;
        Switch switchDefault;


        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamePhone = itemView.findViewById(R.id.tvNamePhone);
            tvDefaultTag = itemView.findViewById(R.id.tvDefaultTag);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvSupport = itemView.findViewById(R.id.tvSupport);
            ivCheck = itemView.findViewById(R.id.ivCheck);
            switchDefault = itemView.findViewById(R.id.switchDefault);
            btnEditAddress = itemView.findViewById(R.id.btnEditAddress);
        }
    }
}
