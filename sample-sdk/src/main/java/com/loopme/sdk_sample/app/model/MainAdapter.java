package com.loopme.sdk_sample.app.model;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loopme.sdk_sample.R;
import com.loopme.sdk_sample.app.views.MainFeaturesFragment;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<CustomViewHolder> {
    private final MainFeaturesFragment.OnItemClickedListener mListener;
    private final List<String> mData = new ArrayList<>();

    public MainAdapter(List<String> list, @NonNull MainFeaturesFragment.OnItemClickedListener listener) {
        mData.addAll(list);
        mListener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new CustomViewHolder(
            LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.main_list_fragment_item, viewGroup, false)
        );
    }

    @Override
    public void onBindViewHolder(CustomViewHolder viewHolder, int position) {
        final String item = mData.get(position);
        viewHolder.textView.setText(item);
        viewHolder.textView.setOnClickListener(v -> mListener.onItemClicked(item));
    }

    @Override
    public int getItemCount() { return mData.size(); }
}
