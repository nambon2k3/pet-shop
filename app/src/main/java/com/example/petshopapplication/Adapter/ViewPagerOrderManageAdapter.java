package com.example.petshopapplication.Adapter;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.petshopapplication.ProcessingTablayoutFragment;
import com.example.petshopapplication.R;
import com.example.petshopapplication.ShippingTablayoutFragment;
import com.example.petshopapplication.WaitingTablayoutFragment;


import lombok.NonNull;

public class ViewPagerOrderManageAdapter extends FragmentStateAdapter {

    private final String[] tabTitles;
    public ViewPagerOrderManageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        tabTitles = fragmentActivity.getResources().getStringArray(R.array.tab_order_manage_titles);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
            {
                Log.d("createFragment", "ProcessingTablayoutFragment");
                return new ProcessingTablayoutFragment(); // Chờ xác nhận
            }
            case 1:
            {
                Log.d("createFragment", "WaitingTablayoutFragment");
                return new WaitingTablayoutFragment(); // Chờ lấy hàng
            }
            case 2:
            {
                Log.d("createFragment", "WaitingTablayoutFragment");
                return new ShippingTablayoutFragment(); // Chờ lấy hàng
            }
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
                return new ProcessingTablayoutFragment(); // Default
        }
    }

    @Override
    public int getItemCount() {
        return tabTitles.length;
    }
}
