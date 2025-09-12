package com.eyadalalimi.students.ui.activity.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.model.FeedItem;

import java.util.ArrayList;
import java.util.List;

public class HomeFeedAdapter extends RecyclerView.Adapter<HomeFeedAdapter.VH> {

    public interface OnItemClick { void onClick(FeedItem item); }

    private final List<FeedItem> items = new ArrayList<>();
    private final OnItemClick click;

    public HomeFeedAdapter(List<FeedItem> initial, OnItemClick click) {
        if (initial != null) items.addAll(initial);
        this.click = click;
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<FeedItem> more) {
        int start = items.size();
        items.addAll(more);
        notifyItemRangeInserted(start, more.size());
    }

    public int getItemCount() { return items.size(); }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_card, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int position) {
        FeedItem it = items.get(position);
        h.title.setText(it.title != null ? it.title : "-");
        String meta = (it.displayType()) + (it.published_at != null ? (" · " + it.published_at) : "");
        h.subtitle.setText(meta);
        h.itemView.setOnClickListener(v -> click.onClick(it));
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            subtitle = itemView.findViewById(R.id.tvSubtitle);
        }
    }
}
