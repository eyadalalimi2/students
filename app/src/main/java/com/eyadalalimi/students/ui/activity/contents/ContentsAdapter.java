package com.eyadalalimi.students.ui.activity.contents;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.model.Content;

import java.util.List;

public class ContentsAdapter extends RecyclerView.Adapter<ContentsAdapter.VH> {

    public interface OnItemClick { void onClick(Content c); }

    private List<Content> items;
    private final OnItemClick click;

    public ContentsAdapter(List<Content> items, OnItemClick click) {
        this.items = items;
        this.click = click;
    }

    public void setItems(List<Content> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_content, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Content c = items.get(position);
        h.title.setText(c.title != null ? c.title : "-");

        // تحديد النوع: نستخدم الحقل type إن وُجد؛ وإلا نستنتج من media.{file_path, source_url}
        String type = (c.type != null && !c.type.isEmpty()) ? c.type
                : (c.media != null && c.media.file_path != null ? "file"
                : (c.media != null && c.media.source_url != null ? "link" : "—"));

        String date = c.published_at != null ? c.published_at : "";
        h.subtitle.setText("النوع: " + type + (date.isEmpty() ? "" : " · " + date));

        h.itemView.setOnClickListener(v -> click.onClick(c));
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
}
