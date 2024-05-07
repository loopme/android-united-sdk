package com.loopme.banner_sample.app.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopme.banner_sample.R;
import com.loopme.banner_sample.app.model.DataProvider;
import com.loopme.banner_sample.app.model.MainAdapter;

public class MainFeaturesFragment extends Fragment {
    private OnItemClickedListener mListener;


    public static MainFeaturesFragment newInstance() {
        return new MainFeaturesFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClickedListener) {
            mListener = (OnItemClickedListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_list_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.main_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MainAdapter adapter = new MainAdapter(DataProvider.getSimpleStringsList());
        adapter.setListener(initOnItemClickListener());
        recyclerView.setAdapter(adapter);
    }

    private OnItemClickedListener initOnItemClickListener() {
        return new OnItemClickedListener() {

            @Override
            public void onItemClicked(String item) {
                if (mListener != null) {
                    mListener.onItemClicked(item);
                }
            }
        };
    }

    public interface OnItemClickedListener {
        void onItemClicked(String item);
    }
}

