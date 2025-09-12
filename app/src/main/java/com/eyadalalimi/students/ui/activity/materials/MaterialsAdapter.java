package com.eyadalalimi.students.ui.activity.materials;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.model.Material;

import java.util.List;

public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.VH> {

    public interface OnItemClick { void onClick(Material m); }

    private final List<Material> data;
    private final OnItemClick click;

    public MaterialsAdapter(List<Material> data, OnItemClick click) {
        this.data = data;
        this.click = click;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_material, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Material m = data.get(pos);
        h.title.setText(m.name != null ? m.name : ("مادة #" + m.id));
        String sub = "نطاق: " + (m.displayScope()) +
                (m.level != null ? (" • مستوى " + m.level) : "");
        h.subtitle.setText(sub);
        h.card.setOnClickListener(v -> { if (click != null) click.onClick(m); });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        CardView card; TextView title; TextView subtitle;
        VH(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            title = itemView.findViewById(R.id.tvTitle);
            subtitle = itemView.findViewById(R.id.tvSub);
        }
    }
}
