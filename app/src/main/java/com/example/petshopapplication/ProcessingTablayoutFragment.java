package com.example.petshopapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petshopapplication.Adapter.OrderAdapter;
import com.example.petshopapplication.databinding.FragmentProcessingTablayoutBinding;
import com.example.petshopapplication.model.Order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessingTablayoutFragment extends Fragment {

    private FragmentProcessingTablayoutBinding binding;
    private RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_processing_tablayout, container, false);

        // Khởi tạo binding
//        binding = FragmentProcessingTablayoutBinding.inflate(inflater, container, false);
//        View view = binding.getRoot();
//        binding.recyclerViewProcessing.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView = view.findViewById(R.id.recycler_view);

        List<Order> orderItems = new ArrayList<>();
        orderItems.add(new Order());
        orderItems.add(new Order());
        orderItems.add(new Order());
        orderItems.add(new Order());
        orderItems.add(new Order());
        orderItems.add(new Order());
        orderItems.add(new Order());
        orderItems.add(new Order());
        orderItems.add(new Order());

        OrderAdapter adapter = new OrderAdapter(orderItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(ProcessingTablayoutFragment.this, LinearLayoutManager.VERTICAL, false));
//        binding.recyclerViewProcessing.setAdapter(adapter);
        return view;
    }

}