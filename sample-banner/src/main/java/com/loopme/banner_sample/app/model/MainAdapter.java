package com.loopme.banner_sample.app.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.loopme.banner_sample.R;
import com.loopme.banner_sample.app.views.MainFeaturesFragment;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.CustomViewHolder> {
    private MainFeaturesFragment.OnItemClickedListener mListener;
    private final List<String> mData = new ArrayList<>();

    public MainAdapter(List<String> list) {
        mData.addAll(list);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_list_fragment_item, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder viewHolder, int position) {
        final String item = mData.get(position);
        viewHolder.textView.setText(item);
        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void onItemClicked(String message) {
        if (mListener != null) {
            mListener.onItemClicked(message);
        }
    }

    public void setListener(MainFeaturesFragment.OnItemClickedListener listener) {
        mListener = listener;
    }

    protected class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        private CustomViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.title);
        }
    }
}
