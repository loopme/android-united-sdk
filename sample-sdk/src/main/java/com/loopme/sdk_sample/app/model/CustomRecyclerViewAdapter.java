package com.loopme.sdk_sample.app.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loopme.sdk_sample.R;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    private final List<CustomListItem> mData = new ArrayList<>();
    private final Context mContext;

    public CustomRecyclerViewAdapter(Context context, ArrayList<CustomListItem> list) {
        mData.addAll(list);
        mContext = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new CustomViewHolder(
            LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.recyclerview_fragment_item, viewGroup, false)
        );
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
        final CustomListItem item = mData.get(position);
        customViewHolder.textView.setText(item.getTitle());
        customViewHolder.imageView.setImageResource(item.getIconId());
        customViewHolder.itemView.setOnClickListener(v -> Toast.makeText(mContext, "Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() { return mData.size(); }
}
