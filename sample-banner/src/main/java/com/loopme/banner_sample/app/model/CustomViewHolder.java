package com.loopme.banner_sample.app.model;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.loopme.banner_sample.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    final TextView textView;
    final ImageView imageView;
    public CustomViewHolder(View view) {
        super(view);
        textView = view.findViewById(R.id.title);
        imageView = view.findViewById(R.id.thumbnail);
    }
}
