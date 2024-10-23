package com.example.petshopapplication.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petshopapplication.R;
import com.example.petshopapplication.model.Province;
import java.util.List;

public class ProvinceAdapter extends RecyclerView.Adapter<ProvinceAdapter.ProvinceViewHolder> {

    private List<Province> provinceList;

    public ProvinceAdapter(List<Province> provinceList) {
        this.provinceList = provinceList;
    }

    @NonNull
    @Override
    public ProvinceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_province, parent, false);
        return new ProvinceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProvinceViewHolder holder, int position) {
        Province province = provinceList.get(position);
        holder.provinceName.setText(province.getName());
    }

    @Override
    public int getItemCount() {
        return provinceList.size();
    }

    public static class ProvinceViewHolder extends RecyclerView.ViewHolder {
        TextView provinceName;

        public ProvinceViewHolder(@NonNull View itemView) {
            super(itemView);
            provinceName = itemView.findViewById(R.id.province_name);
        }
    }
}
