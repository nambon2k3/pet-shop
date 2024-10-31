package com.example.petshopapplication.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.petshopapplication.ProcessingTablayoutFragment;
import com.example.petshopapplication.R;
import com.example.petshopapplication.WaitingTablayoutFragment;

import java.util.HashMap;

import lombok.NonNull;

public class ViewPagerOrderManageAdapter extends FragmentStateAdapter {
    private final HashMap<String, Fragment> fragmentMap = new HashMap<>();
    private String[] tabTitles;

    public ViewPagerOrderManageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        String[] tabInfo = fragmentActivity.getResources().getStringArray(R.array.tab_order_manage_titles);
        tabTitles = new String[tabInfo.length];

        // Duyệt qua từng phần tử trong tabInfo và tách tên tab và Fragment
        for (int i = 0; i < tabInfo.length; i++) {
            String[] parts = tabInfo[i].split("\\|"); // Tách tên tab và tên Fragment bằng ký tự |
            String tabTitle = parts[0];
            String fragmentName = parts[1];
            tabTitles[i] = tabTitle;

            // Ánh xạ tên Fragment với đối tượng Fragment tương ứng
            fragmentMap.put(tabTitle, createFragmentByName(fragmentName));
        }
    }

    // Method to create fragments based on their names:
    private Fragment createFragmentByName(String fragmentName) {
//        switch (fragmentName) {
//            case "ProcessingTablayoutFragment":
//                return new ProcessingTablayoutFragment();
//            case "WaitingTablayoutFragment":
//                return new WaitingTablayoutFragment();
//            case "ShippingFragment":
//                return new ShippingFragment();
//            case "DeliveredFragment":
//                return new DeliveredFragment();
//            case "CanceledFragment":
//                return new CanceledFragment();
//            default:
//                return new ProcessingTablayoutFragment();
//        }

        // Dùng cùng một Fragment (ProcessingTablayoutFragment) cho tất cả các tab
        return new ProcessingTablayoutFragment();    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Lấy tên tab từ mảng và trả về Fragment tương ứng từ fragmentMap
        String tabTitle = tabTitles[position];
        return fragmentMap.getOrDefault(tabTitle, new ProcessingTablayoutFragment());
    }

    @Override
    public int getItemCount() {
        return tabTitles.length;
    }
}
