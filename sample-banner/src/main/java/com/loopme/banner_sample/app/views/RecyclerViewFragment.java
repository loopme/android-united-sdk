package com.loopme.banner_sample.app.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopme.LoopMeBanner;
import com.loopme.NativeVideoBinder;
import com.loopme.NativeVideoRecyclerAdapter;
import com.loopme.banner_sample.R;
import com.loopme.banner_sample.app.model.CustomRecyclerViewAdapter;
import com.loopme.banner_sample.app.model.DataProvider;
import com.loopme.common.LoopMeError;

public class RecyclerViewFragment extends Fragment implements LoopMeBanner.Listener {
    private static final int POSITION_IN_LIST = 2;
    private NativeVideoRecyclerAdapter mNativeVideoRecyclerAdapter;

    public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recyclerview_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        CustomRecyclerViewAdapter adapter = new CustomRecyclerViewAdapter(getContext(), DataProvider.getCustomListItem());
        mNativeVideoRecyclerAdapter = new NativeVideoRecyclerAdapter(adapter, getActivity(), recyclerView);
        mNativeVideoRecyclerAdapter.putAdWithAppKeyToPosition(LoopMeBanner.TEST_MPU_BANNER, POSITION_IN_LIST);
        NativeVideoBinder binder = new NativeVideoBinder.Builder(R.layout.ad_banner_view)
                .setLoopMeBannerViewId(R.id.loop_me_banner_view)
                .build();
        mNativeVideoRecyclerAdapter.setViewBinder(binder);

        recyclerView.setAdapter(mNativeVideoRecyclerAdapter);
        mNativeVideoRecyclerAdapter.setListener(this);
        mNativeVideoRecyclerAdapter.loadAds();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNativeVideoRecyclerAdapter != null) {
            mNativeVideoRecyclerAdapter.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNativeVideoRecyclerAdapter != null) {
            mNativeVideoRecyclerAdapter.onResume();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mNativeVideoRecyclerAdapter != null) {
            mNativeVideoRecyclerAdapter.destroy();
        }
    }

    @Override
    public void onLoopMeBannerLoadSuccess(LoopMeBanner banner) {
        Toast.makeText(getContext(), "Ad Loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error) {
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoopMeBannerShow(LoopMeBanner banner) {
    }

    @Override
    public void onLoopMeBannerHide(LoopMeBanner banner) {
    }

    @Override
    public void onLoopMeBannerClicked(LoopMeBanner banner) {
    }

    @Override
    public void onLoopMeBannerLeaveApp(LoopMeBanner banner) {
    }

    @Override
    public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner banner) {
    }

    @Override
    public void onLoopMeBannerExpired(LoopMeBanner banner) {
    }
}
