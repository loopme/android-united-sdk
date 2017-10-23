package com.loopme.tester.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopme.tester.R;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.ui.adapters.AdSpotAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by katerina on 2/12/17.
 */

public class AdSpotListFragment extends BaseFragment implements
        AdSpotAdapter.AdAdapterCallback {

    private List<AdSpot> mAdSpotList = new ArrayList<>();
    private AdSpotAdapter mAdSpotAdapter;
    private Context mContext;
    private OnAdSpotListFragmentListener mOnAdSpotListFragmentListener;
    private long TIME_DELAY = 100;
    private RecyclerView mAppKeysRecyclerView;


    public AdSpotListFragment() {
    }

    public static AdSpotListFragment newInstance() {
        return new AdSpotListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_adspot_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initAdSpotsLoader();
        mAppKeysRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_adspot_recycler_view);
        mAdSpotAdapter = new AdSpotAdapter(mContext, mAdSpotList, this);

        mAppKeysRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAppKeysRecyclerView.setAdapter(mAdSpotAdapter);
        requestFocus();
    }

    private void initAdSpotsLoader() {
        if (mOnAdSpotListFragmentListener != null) {
            mOnAdSpotListFragmentListener.onUpdateList();
        }
    }

    @Override
    public void onDelete(AdSpot adSpot) {
        if (mOnAdSpotListFragmentListener != null) {
            mOnAdSpotListFragmentListener.onDelete(adSpot);
        }
    }

    @Override
    public void onItemSelected(AdSpot adSpot) {
        if (mOnAdSpotListFragmentListener != null) {
            mOnAdSpotListFragmentListener.onItemSelected(adSpot);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mOnAdSpotListFragmentListener = (OnAdSpotListFragmentListener) context;
    }

    @Override
    public void onDetach() {
        mOnAdSpotListFragmentListener = null;
        super.onDetach();
    }

    public void updateList(List<AdSpot> adSpotList) {
        if (mAdSpotAdapter != null) {
            mAdSpotAdapter.setList(adSpotList);
        }
    }

    public void requestFocus() {
        if (mAppKeysRecyclerView != null) {
            mAppKeysRecyclerView.requestFocus();
        }
    }

    public interface OnAdSpotListFragmentListener {
        void onItemSelected(AdSpot adSpot);

        void onDelete(AdSpot adSpot);

        void onUpdateList();
    }

    public void refreshView() {
        postRunnableDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAdSpotAdapter != null) {
                    mAdSpotAdapter.notifyDataSetChanged();
                }
            }
        }, TIME_DELAY);
    }
}
