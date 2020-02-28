package com.loopme;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

import com.loopme.common.AdChecker;
import com.loopme.common.LoopMeError;
import com.loopme.tracker.partners.LoopMeTracker;
import com.loopme.utils.Utils;

class NativeVideoController {

    private static final String LOG_TAG = NativeVideoController.class.getSimpleName();

    private static final int FIRST_POSITION = 0;
    private int mItemCount;
    private Activity mActivity;
    private MinimizedMode mMinimizedMode;
    private LoopMeBanner.Listener mAdListener;
    private NativeVideoBinder mNativeBinder;
    private SparseArray<View> mViewMap = new SparseArray<View>();
    private SparseArray<String> mAppKeysMap = new SparseArray<String>();
    private SparseArray<LoopMeBanner> mAdsMap = new SparseArray<LoopMeBanner>();
    private DataChangeListener mDataChangeListener;
    private AdChecker mAdChecker;

    public NativeVideoController(Activity activity, AdChecker checker) {
        mActivity = activity;
        mAdChecker = checker;
        Utils.init(activity);
    }

    public void refreshAdPlacement(int itemCount) {
        if (itemCount == 0) {
            destroyBannerMap();
        } else if (itemCount < mItemCount) {
            cleanAdMapBecauseOfNewListLength(itemCount);
        }
    }

    private void cleanAdMapBecauseOfNewListLength(int newListLength) {
        SparseArray<LoopMeBanner> cloneMap = mAdsMap.clone();
        mAdsMap.clear();
        for (int i = 0; i < cloneMap.size(); i++) {
            int key = cloneMap.keyAt(i);
            LoopMeBanner banner = cloneMap.get(key);
            if (key <= newListLength) {
                mAdsMap.put(key, banner);
            } else {
                banner.destroy();
            }
        }
    }

    public void onResume(RecyclerView recyclerView) {
        onScroll(recyclerView);
    }

    public void onPause() {
        pauseBanners();
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

    public void setViewBinder(NativeVideoBinder binder) {
        mNativeBinder = binder;
    }

    public void setListener(LoopMeBanner.Listener listener) {
        mAdListener = listener;
    }

    public LoopMeBanner getNativeVideoAd(int position) {
        return mAdsMap.get(position);
    }

    protected int getAdsCount() {
        return mAdsMap.size();
    }

    protected View getAdView(LayoutInflater inflater, int position) {
        Logging.out(LOG_TAG, "getAdView");

        if (mViewMap.indexOfKey(position) >= 0) {
            return mViewMap.get(position);
        }
        View view;

        if (mNativeBinder != null) {
            view = inflater.inflate(mNativeBinder.getLayout(), null, false);
            bindDataToView(view, mNativeBinder, position);
        } else {
            Logging.out(LOG_TAG, "Error: NativeVideoBinder is null. Init and bind it");
            view = null;
        }

        mViewMap.put(position, view);
        return view;
    }

    private void bindDataToView(View row, NativeVideoBinder binder, final int position) {
        Logging.out(LOG_TAG, "bindDataToView");
        LoopMeBannerView video = (LoopMeBannerView) row.findViewById(binder.getBannerViewId());
        int index = mAdsMap.indexOfKey(position);
        LoopMeBanner banner = mAdsMap.valueAt(index);
        banner.bindView(video);
        banner.showNativeVideo();
    }

    public void loadAds(final int itemsCount, DataChangeListener listener) {
        mDataChangeListener = listener;
        mItemCount = itemsCount;

        if (mAppKeysMap.size() == 0) {
            LoopMeTracker.post("No ads added for loading");
        } else {
            initBanners();
        }
    }

    private void addItem(LoopMeBanner banner, int itemsCount) {
        int indexOfValue = mAppKeysMap.indexOfValue(banner.getAppKey());
        if (isValidIndex(indexOfValue)) {
            int key = mAppKeysMap.keyAt(indexOfValue);
            if (key < itemsCount + getAdsCount()) {
                mAdsMap.put(key, banner);
                onDataSetChanged();
            }
        }
    }

    private boolean isValidIndex(int indexOfValue) {
        return indexOfValue >= 0 && indexOfValue < mAppKeysMap.size();
    }

    private void onDataSetChanged() {
        if (mDataChangeListener != null) {
            mDataChangeListener.onDataSetChanged();
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

    public void onScroll(RecyclerView recyclerView) {
        if (recyclerView != null && mAdsMap.size() >= 0) {
            int[] positions = Utils.getPositionsOnScreen(recyclerView);

            if (isPositionsArrayValid(positions)) {
                checkAdMapVisibility(positions, recyclerView);
            }
        }
    }

    private void checkAdMapVisibility(int[] positions, RecyclerView recyclerView) {
        for (int i = 0; i < mAdsMap.size(); i++) {
            int adIndex = mAdsMap.keyAt(i);
            if (isAd(adIndex) && !isInFullScreen(adIndex)) {
                checkAdVisibility(adIndex, positions, recyclerView);
            }
        }
    }

    private boolean isAd(int adIndex) {
        return mAdChecker != null && mAdChecker.isAd(adIndex);
    }

    private void checkAdVisibility(int adIndex, int[] positions, RecyclerView recyclerView) {
        int first = positions[0];
        int last = positions[1];

        LoopMeBanner banner = mAdsMap.get(adIndex);

        if (isAdOnTheScreen(adIndex, first, last)) {
            handleAdOnScreen(adIndex, recyclerView);
        } else {
            handleAdOutOfScreen(banner);
        }
    }

    private boolean isAdOnTheScreen(int adIndex, int first, int last) {
        return first <= adIndex && adIndex <= last;
    }

    private boolean isInFullScreen(int adIndex) {
        return mAdsMap.get(adIndex).isFullScreenMode();
    }

    private void handleAdOnScreen(final int adIndex, RecyclerView recyclerView) {
        NativeVideoRecyclerAdapter.NativeVideoViewHolder viewHolder =
                (NativeVideoRecyclerAdapter.NativeVideoViewHolder)
                        recyclerView.findViewHolderForAdapterPosition(adIndex);

        checkFiftyPercentVisibility(viewHolder.getView(), adIndex);
    }

    private void checkFiftyPercentVisibility(final View view, final int adIndex) {
        MoatViewAbilityUtils.calculateViewAbilitySyncDelayed(view, new MoatViewAbilityUtils.OnResultListener() {

            @Override
            public void onResult(final MoatViewAbilityUtils.ViewAbilityInfo info) {
                onVisibilityResult(info, adIndex);
            }
        });
    }

    private void onVisibilityResult(MoatViewAbilityUtils.ViewAbilityInfo info, int adIndex) {
        LoopMeBanner banner = mAdsMap.get(adIndex);
        if (info.isVisibleMore50Percents()) {
            Logging.out(LOG_TAG, "visible more than 50%");
            banner.switchToNormalMode();
            resumeBanner(banner);
        } else {
            Logging.out(LOG_TAG, "visible less than 50%");
            handleAdOutOfScreen(banner);
        }
    }

    private void handleAdOutOfScreen(LoopMeBanner banner) {
        if (mAdsMap.size() == 1) {
            switchToMinimizedMode(banner);
        } else {
            pauseBanner(banner);
        }
    }

    private void destroyBannerMap() {
        for (int i = 0; i < mAdsMap.size(); i++) {
            LoopMeBanner banner = mAdsMap.valueAt(i);
            if (banner != null) {
                banner.destroy();
            }
        }
        mAdsMap.clear();
    }

    private void pauseBanners() {
        for (int i = 0; i < mAdsMap.size(); i++) {
            LoopMeBanner banner = mAdsMap.valueAt(i);
            pauseBanner(banner);
        }
    }

    private void initBanners() {
        LoopMeBanner.Listener bannerListener = initBannerListener();
        for (int i = 0; i < mAppKeysMap.size(); i++) {
            String appKey = mAppKeysMap.valueAt(i);
            LoopMeBanner banner = LoopMeBanner.getInstance(appKey, mActivity);
            banner.setListener(bannerListener);
            banner.setAutoLoading(false);
            banner.load();
        }
    }

    private void switchToMinimizedMode(LoopMeBanner banner) {
        if (banner != null) {
            banner.switchToMinimizedMode();
        }
    }

    private void pauseBanner(LoopMeBanner banner) {
        if (banner != null) {
            banner.pause();
        }
    }

    private void resumeBanner(LoopMeBanner banner) {
        if (banner != null) {
            banner.resume();
        }
    }

    public void setMinimizedMode(MinimizedMode mode) {
        mMinimizedMode = mode;
        mMinimizedMode.setPosition(mAppKeysMap.keyAt(FIRST_POSITION));
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
            public void onLoopMeBannerLoadFail(LoopMeBanner banner, LoopMeError error) {
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
        };
    }

    private boolean isPositionsArrayValid(int[] positions) {
        return positions[0] != -1 && positions[1] != -1;
    }

    public interface DataChangeListener {
        void onDataSetChanged();
    }
}
