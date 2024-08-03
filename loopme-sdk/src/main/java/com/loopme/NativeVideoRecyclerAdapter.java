package com.loopme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loopme.common.AdChecker;

public class NativeVideoRecyclerAdapter extends RecyclerView.Adapter
        implements AdChecker, NativeVideoController.DataChangeListener {

    private static final String LOG_TAG = NativeVideoRecyclerAdapter.class.getSimpleName();

    static final int TYPE_AD = 1000;

    private final RecyclerView.Adapter mOriginAdapter;
    private final NativeVideoController mNativeVideoController;
    private final LayoutInflater mInflater;

    private final RecyclerView mRecyclerView;

    public NativeVideoRecyclerAdapter(
        RecyclerView.Adapter originAdapter, Activity activity, RecyclerView recyclerView
    ) {
        if (originAdapter == null || activity == null || recyclerView == null) {
            throw new IllegalArgumentException("Some of parameters is null");
        }
        mOriginAdapter = originAdapter;
        mRecyclerView = recyclerView;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mNativeVideoController = new NativeVideoController(activity, this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mNativeVideoController.onScroll(recyclerView);
            }
        });
        mRecyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            Logging.out(LOG_TAG, "onLayoutChange!!!!!");
            mNativeVideoController.onScroll(mRecyclerView);
        });
        mOriginAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() { triggerUpdateProcessor(); }
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) { triggerUpdateProcessor(); }
            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) { triggerUpdateProcessor(); }
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) { triggerUpdateProcessor(); }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) { triggerUpdateProcessor(); }
            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) { triggerUpdateProcessor(); }
        });
    }

    private void triggerUpdateProcessor() {
        notifyDataSetChanged();
        mNativeVideoController.refreshAdPlacement(mOriginAdapter.getItemCount());
    }

    /**
     * Clean resources.
     */
    public void destroy() { mNativeVideoController.destroy(); }

    /**
     * Pauses ads (video).
     * NOTE: trigger in Activity onPause().
     */
    public void onPause() {
        Logging.out(LOG_TAG, "onPause");
        mNativeVideoController.onPause();
    }

    /**
     * Resumes ads (video).
     * NOTE: trigger in Activity onResume().
     */
    public void onResume() {
        Logging.out(LOG_TAG, "onResume");
        mNativeVideoController.onResume(mRecyclerView);
    }

    public void setMinimizedMode(MinimizedMode mode) {
        if (mode != null) {
            Logging.out(LOG_TAG, "Set minimized mode");
            mNativeVideoController.setMinimizedMode(mode);
        }
    }

    /**
     * Adds banner ad to defined position.
     * @param appKey   - app key
     * @param position - position in list
     */
    public void putAdWithAppKeyToPosition(String appKey, int position) {
        mNativeVideoController.putAdWithAppKeyToPosition(
            appKey,
            position < 0 ? 0 : Math.min(position, mOriginAdapter.getItemCount())
        );
    }

    /**
     * Starts loading all ads which were added with 'putAdWithAppKeyToPosition' method
     */
    public void loadAds() {
        if (mNativeVideoController != null) {
            mNativeVideoController.loadAds(mOriginAdapter.getItemCount(), this);
        }
    }

    /**
     * Define custome design for TileText ads
     * @param binder - ViewBinder
     */
    public void setViewBinder(NativeVideoBinder binder) {
        if (mNativeVideoController != null) {
            mNativeVideoController.setViewBinder(binder);
        }
    }

    /**
     * Set listener to receive notifications during loading/showing process
     * @param listener - listener
     */
    public void setListener(LoopMeBanner.Listener listener) {
        mNativeVideoController.setListener(listener);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RelativeLayout layout = new RelativeLayout(viewGroup.getContext());
        layout.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return viewType == TYPE_AD ?
            new NativeVideoViewHolder(layout) :
            mOriginAdapter.onCreateViewHolder(viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        if (isAd(position)) {
            Logging.out(LOG_TAG, "onBindViewHolder");
            View rowView = mNativeVideoController.getAdView(mInflater, position);
            ((NativeVideoViewHolder) viewHolder).removeAllViews();
            if (rowView.getParent() != null) {
                ((ViewGroup) rowView.getParent()).removeView(rowView);
            }
            ((NativeVideoViewHolder) viewHolder).addView(rowView);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rowView.getLayoutParams();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            rowView.setLayoutParams(layoutParams);
        } else {
            int initPosition = mNativeVideoController.getInitialPosition(position);
            mOriginAdapter.onBindViewHolder(viewHolder, initPosition);
        }
    }

    @Override
    public boolean isAd(int i) { return mNativeVideoController.getNativeVideoAd(i) != null; }

    @Override
    public int getItemViewType(int position) {
        int initPosition = mNativeVideoController.getInitialPosition(position);
        return isAd(position) ? TYPE_AD : mOriginAdapter.getItemViewType(initPosition);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) { mOriginAdapter.setHasStableIds(hasStableIds); }

    @Override
    public long getItemId(int position) {
        return isAd(position) ?
            -System.identityHashCode(mNativeVideoController.getNativeVideoAd(position)) :
            mOriginAdapter.getItemId(mNativeVideoController.getInitialPosition(position));
    }

    @Override
    public int getItemCount() { return mOriginAdapter.getItemCount() + mNativeVideoController.getAdsCount(); }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        mOriginAdapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
        return mOriginAdapter.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        mOriginAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        mOriginAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        mOriginAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        mOriginAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mOriginAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mOriginAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onDataSetChanged() { mOriginAdapter.notifyDataSetChanged(); }

    protected static class NativeVideoViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout adView;

        private NativeVideoViewHolder(@NonNull  View view) {
            super(view);
            adView = (RelativeLayout) view;
        }

        private void addView(@NonNull View view) { adView.addView(view); }
        public View getView() { return adView; }
        private void removeAllViews() { adView.removeAllViews(); }
    }
}
