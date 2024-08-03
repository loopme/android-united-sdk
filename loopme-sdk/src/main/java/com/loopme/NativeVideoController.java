package com.loopme;

import static com.loopme.debugging.Params.ERROR_MSG;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.loopme.common.AdChecker;
import com.loopme.common.LoopMeError;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class NativeVideoController {

    private static final String LOG_TAG = NativeVideoController.class.getSimpleName();
    private int mItemCount;
    private final Activity mActivity;
    private MinimizedMode mMinimizedMode;
    private LoopMeBanner.Listener mAdListener;
    private NativeVideoBinder mNativeBinder;
    private final SparseArray<View> mViewMap = new SparseArray<View>();
    private final SparseArray<String> mAppKeysMap = new SparseArray<String>();
    private final SparseArray<LoopMeBanner> mAdsMap = new SparseArray<LoopMeBanner>();
    private DataChangeListener mDataChangeListener;
    private final AdChecker mAdChecker;

    public NativeVideoController(@NonNull Activity activity, @NonNull AdChecker checker) {
        mActivity = activity;
        mAdChecker = checker;
        Utils.init(mActivity);
    }

    public void refreshAdPlacement(int itemCount) {
        if (itemCount == 0) {
            destroyBannerMap();
            return;
        }
        if (itemCount >= mItemCount) {
            return;
        }
        SparseArray<LoopMeBanner> cloneMap = mAdsMap.clone();
        mAdsMap.clear();
        for (int i = 0; i < cloneMap.size(); i++) {
            int key = cloneMap.keyAt(i);
            LoopMeBanner banner = cloneMap.get(key);
            if (key <= itemCount) {
                mAdsMap.put(key, banner);
            } else {
                banner.destroy();
            }
        }
    }

    public void onResume(RecyclerView recyclerView) { onScroll(recyclerView); }

    public void onPause() {
        for (int i = 0; i < mAdsMap.size(); i++) {
            LoopMeBanner banner = mAdsMap.valueAt(i);
            if (banner != null) banner.pause();
        }
    }

    public void destroy() {
        destroyBannerMap();
        mViewMap.clear();
        mAppKeysMap.clear();
        mAdListener = null;
        mDataChangeListener = null;
    }

    public void putAdWithAppKeyToPosition(String appKey, int position) {
        Logging.out(LOG_TAG, "putAdWithAppKeyToPosition " + appKey + " " + position);
        mAppKeysMap.put(position, appKey);
    }

    public void setViewBinder(NativeVideoBinder binder) { mNativeBinder = binder; }

    public void setListener(LoopMeBanner.Listener listener) { mAdListener = listener; }

    public LoopMeBanner getNativeVideoAd(int position) { return mAdsMap.get(position); }

    protected int getAdsCount() { return mAdsMap.size(); }

    protected View getAdView(LayoutInflater inflater, int position) {
        Logging.out(LOG_TAG, "getAdView");

        if (mViewMap.indexOfKey(position) >= 0) {
            return mViewMap.get(position);
        }
        View view;

        if (mNativeBinder != null) {
            view = inflater.inflate(mNativeBinder.getLayout(), null, false);
            Logging.out(LOG_TAG, "bindDataToView");
            LoopMeBannerView video = view.findViewById(mNativeBinder.getBannerViewId());
            int index = mAdsMap.indexOfKey(position);
            LoopMeBanner banner = mAdsMap.valueAt(index);
            banner.bindView(video);
            banner.showNativeVideo();
        } else {
            Logging.out(LOG_TAG, "Error: NativeVideoBinder is null. Init and bind it");
            view = null;
        }

        mViewMap.put(position, view);
        return view;
    }

    public void loadAds(final int itemsCount, DataChangeListener listener) {
        mDataChangeListener = listener;
        mItemCount = itemsCount;
        if (mAppKeysMap.size() == 0) {
            HashMap<String, String> errorInfo = new HashMap<>();
            errorInfo.put(ERROR_MSG, "No ads added for loading");
            LoopMeTracker.post(errorInfo);
            return;
        }
        LoopMeBanner.Listener bannerListener = initBannerListener();
        for (int i = 0; i < mAppKeysMap.size(); i++) {
            String appKey = mAppKeysMap.valueAt(i);
            LoopMeBanner banner = LoopMeBanner.getInstance(appKey, mActivity);
            banner.setListener(bannerListener);
            banner.setAutoLoading(false);
            banner.load();
        }
    }

    private void addItem(LoopMeBanner banner, int itemsCount) {
        int indexOfValue = mAppKeysMap.indexOfValue(banner.getAppKey());
        boolean isValidIndex = indexOfValue >= 0 && indexOfValue < mAppKeysMap.size();
        if (!isValidIndex) {
            return;
        }
        int key = mAppKeysMap.keyAt(indexOfValue);
        if (key < itemsCount + getAdsCount()) {
            mAdsMap.put(key, banner);
            if (mDataChangeListener != null) {
                mDataChangeListener.onDataSetChanged();
            }
        }
    }

    public int getInitialPosition(int position) {
        int adsBefore = 0;
        for (int i = 0; i < mAdsMap.size(); i++) {
            if (mAdsMap.keyAt(i) <= position) {
                adsBefore++;
            }
        }
        return position - adsBefore;
    }

    private int[] getPositionsOnScreen(RecyclerView recyclerView) {
        int[] positions = {-1, -1};
        if (recyclerView == null) {
            return positions;
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            positions[0] = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            positions[1] = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            positions[0] = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
            positions[1] = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] firsts;
            int[] lasts;
            try {
                firsts = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
                lasts = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            } catch (NullPointerException e) {
                return positions;
            }

            List<Integer> firstList = new ArrayList<>(firsts.length);
            for (int first : firsts) {
                firstList.add(first);
            }

            List<Integer> lastList = new ArrayList<>(lasts.length);
            for (int last : lasts) {
                lastList.add(last);
            }

            positions[0] = Collections.min(firstList);
            positions[1] = Collections.max(lastList);
        }
        return positions;
    }

    public void onScroll(RecyclerView recyclerView) {
        if (recyclerView == null || mAdsMap.size() <= 0) {
            return;
        }
        int[] positions = getPositionsOnScreen(recyclerView);
        boolean isPositionsArrayValid = positions[0] != -1 && positions[1] != -1;
        if (!isPositionsArrayValid) {
            return;
        }
        for (int i = 0; i < mAdsMap.size(); i++) {
            int adIndex = mAdsMap.keyAt(i);
            if (mAdChecker.isAd(adIndex) && !mAdsMap.get(adIndex).isFullScreenMode()) {
                boolean isAdOnTheScreen = positions[0] <= adIndex && adIndex <= positions[1];
                if (isAdOnTheScreen) {
                    handleAdOnScreen(adIndex, recyclerView);
                } else {
                    handleAdOutOfScreen(mAdsMap.get(adIndex));
                }
            }
        }
    }

    private void handleAdOnScreen(final int adIndex, RecyclerView recyclerView) {
        NativeVideoRecyclerAdapter.NativeVideoViewHolder viewHolder =
                (NativeVideoRecyclerAdapter.NativeVideoViewHolder)
                        recyclerView.findViewHolderForAdapterPosition(adIndex);
        View view = viewHolder.getView();
        ViewAbilityUtils.calculateViewAbilitySyncDelayed(view, info -> {
            LoopMeBanner banner = mAdsMap.get(adIndex);
            if (info.isVisibleMore50Percents()) {
                Logging.out(LOG_TAG, "visible more than 50%");
                banner.switchToNormalMode();
                banner.resume();
            } else {
                Logging.out(LOG_TAG, "visible less than 50%");
                handleAdOutOfScreen(banner);
            }
        });
    }

    private void handleAdOutOfScreen(LoopMeBanner banner) {
        if (banner == null) {
            return;
        }
        if (mAdsMap.size() == 1) {
            banner.switchToMinimizedMode();
        } else {
            banner.pause();
        }
    }

    private void destroyBannerMap() {
        for (int i = 0; i < mAdsMap.size(); i++) {
            LoopMeBanner banner = mAdsMap.valueAt(i);
            if (banner != null) banner.destroy();
        }
        mAdsMap.clear();
    }

    public void setMinimizedMode(MinimizedMode mode) {
        mMinimizedMode = mode;
        mMinimizedMode.setPosition(mAppKeysMap.keyAt(0));
    }

    private LoopMeBanner.Listener initBannerListener() {
        return new LoopMeBanner.Listener() {
            @Override
            public void onLoopMeBannerLoadSuccess(LoopMeBanner banner) {
                banner.setMinimizedMode(mMinimizedMode);
                addItem(banner, mItemCount);
                if (mAdListener != null) {
                    mAdListener.onLoopMeBannerLoadSuccess(banner);
                }
            }
            @Override
            public void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error) { }
            @Override
            public void onLoopMeBannerShow(LoopMeBanner banner) { }
            @Override
            public void onLoopMeBannerHide(LoopMeBanner banner) { }
            @Override
            public void onLoopMeBannerClicked(LoopMeBanner banner) { }
            @Override
            public void onLoopMeBannerLeaveApp(LoopMeBanner banner) { }
            @Override
            public void onLoopMeBannerVideoDidReachEnd(LoopMeBanner banner) { }
            @Override
            public void onLoopMeBannerExpired(LoopMeBanner banner) { }
        };
    }

    public interface DataChangeListener {
        void onDataSetChanged();
    }
}
