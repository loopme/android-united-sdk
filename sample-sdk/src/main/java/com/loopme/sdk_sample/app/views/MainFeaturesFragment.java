package com.loopme.sdk_sample.app.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopme.sdk_sample.R;
import com.loopme.sdk_sample.app.model.Constants;
import com.loopme.sdk_sample.app.model.MainAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainFeaturesFragment extends Fragment {
    private OnItemClickedListener mListener;

    public static MainFeaturesFragment newInstance() { return new MainFeaturesFragment(); }

    @Override
    public void onAttach(@NonNull Context context) {
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<String> dataList = new ArrayList<>();
        dataList.add(Constants.SIMPLE);
        dataList.add(Constants.RECYCLERVIEW);
        RecyclerView recyclerView = view.findViewById(R.id.main_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MainAdapter(dataList, item -> mListener.onItemClicked(item)));
    }

    public interface OnItemClickedListener {
        void onItemClicked(String item);
    }
}

