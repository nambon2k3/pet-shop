package com.example.petshopapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import com.example.petshopapplication.Adapter.ViewPagerOrderManageAdapter;
import com.example.petshopapplication.databinding.ActivityListOrderManageBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class ListOrderManageActivity extends AppCompatActivity {
    ActivityListOrderManageBinding binding;
    private String[] tabTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_order_manage);

        binding = ActivityListOrderManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tabTitles = getResources().getStringArray(R.array.tab_order_manage_inventory_titles);
        initTablayouts();
    }

    private void initTablayouts() {
        // Find TabLayout and ViewPager2 in the layout
        TabLayout tabLayout = binding.tabLayout;
        ViewPager2 viewPager = binding.viewPager;

        // Create ViewPagerAdapter and set it to ViewPager2
        ViewPagerOrderManageAdapter viewPagerAdapter = new ViewPagerOrderManageAdapter(this, true);
        viewPager.setAdapter(viewPagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Check if the position is valid
            if (position >= 0 && position < tabTitles.length) {
                tab.setText(tabTitles[position]);
            } else {
                tab.setText("Tab " + position); // default name if out of range
            }
        }).attach();
    }
}