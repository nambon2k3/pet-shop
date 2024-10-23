package com.example.petshopapplication.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.petshopapplication.fragment.AwaitingPickupFragment;
import com.example.petshopapplication.fragment.PendingConfirmationFragment;

import lombok.NonNull;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PendingConfirmationFragment(); // Chờ xác nhận
            case 1:
                return new AwaitingPickupFragment(); // Chờ lấy hàng
//            case 2:
//                return new InTransitFragment(); // Đang giao
//            case 3:
//                return new DeliveredFragment(); // Đã giao
//            case 4:
//                return new CanceledFragment(); // Đơn hủy
//            case 5:
//                return new ReturnedRefundedFragment(); // Trả hàng/Hoàn tiền
//            case 6:
//                return new FailedDeliveryFragment(); // Giao không thành công
            default:
                return new PendingConfirmationFragment(); // Default
        }
    }

    @Override
    public int getItemCount() {
        return 7; // Số lượng tab
    }
}
