package com.eyadalalimi.students.ui.activity.assets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.model.Asset;

import java.util.List;

public class AssetsAdapter extends RecyclerView.Adapter<AssetsAdapter.VH> {

    public interface OnItemClick {
        void onClick(Asset a);
    }

    private List<Asset> items;
    private final OnItemClick click;

    public AssetsAdapter(List<Asset> items, OnItemClick click) {
        this.items = items;
        this.click = click;
    }

    public void setItems(List<Asset> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asset, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int position) {
        Asset a = items.get(position);
        h.title.setText(a.title != null ? a.title : "-");
        h.subtitle.setText(buildSubtitle(a));
        h.itemView.setOnClickListener(v -> click.onClick(a));
    }

    @Override public int getItemCount() { return items != null ? items.size() : 0; }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            subtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }

    private String buildSubtitle(Asset a) {
        String type = a.category != null ? a.category : "—";
        String date = a.published_at != null ? a.published_at : "";
        return "النوع: " + type + (date.isEmpty() ? "" : " · " + date);
    }
}
