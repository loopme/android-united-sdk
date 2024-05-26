package com.loopme.banner_sample.app.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.loopme.banner_sample.R;

import java.util.ArrayList;
import java.util.List;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.CustomViewHolder> {

    private final List<CustomListItem> mData = new ArrayList<>();
    private final Context mContext;

    public CustomRecyclerViewAdapter(Context context, ArrayList<CustomListItem> list) {
        mData.addAll(list);
        mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_fragment_item, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
        final CustomListItem item = mData.get(position);
        customViewHolder.textView.setText(item.getTitle());
        customViewHolder.imageView.setImageResource(item.getIconId());
        customViewHolder.itemView.setOnClickListener(initClickListener(item));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private View.OnClickListener initClickListener(final CustomListItem item) {
        return v -> Toast.makeText(mContext, "Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
    }

    protected class CustomViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;

        private CustomViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.thumbnail);
            this.textView = view.findViewById(R.id.title);
        }
    }
}
