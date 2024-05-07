package com.loopme;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.loopme.common.AdChecker;

public class NativeVideoRecyclerAdapter extends RecyclerView.Adapter
        implements AdChecker, NativeVideoController.DataChangeListener {

    private static final String LOG_TAG = NativeVideoRecyclerAdapter.class.getSimpleName();

    static final int TYPE_AD = 1000;

    private final RecyclerView.Adapter mOriginAdapter;
    private final NativeVideoController mNativeVideoController;
    private final Activity mActivity;
    private final LayoutInflater mInflater;

    private final RecyclerView mRecyclerView;

    public NativeVideoRecyclerAdapter(RecyclerView.Adapter originAdapter,
                                      Activity activity,
                                      RecyclerView recyclerView) {

        if (originAdapter == null || activity == null || recyclerView == null) {
            throw new IllegalArgumentException("Some of parameters is null");
        }

        mActivity = activity;
        mOriginAdapter = originAdapter;
        mRecyclerView = recyclerView;

        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mNativeVideoController = new NativeVideoController(mActivity, this);
        recyclerView.addOnScrollListener(initOnScrollListener());
        mRecyclerView.addOnLayoutChangeListener(initLayoutChangeListener());
        mOriginAdapter.registerAdapterDataObserver(initAdapterObserver());
    }

    private RecyclerView.OnScrollListener initOnScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mNativeVideoController.onScroll(recyclerView);
            }
        };
    }

    private View.OnLayoutChangeListener initLayoutChangeListener() {
        return new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Logging.out(LOG_TAG, "onLayoutChange!!!!!");
                mNativeVideoController.onScroll(mRecyclerView);
            }
        };
    }

    private RecyclerView.AdapterDataObserver initAdapterObserver() {
        return new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                triggerUpdateProcessor();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                triggerUpdateProcessor();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                triggerUpdateProcessor();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                triggerUpdateProcessor();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                triggerUpdateProcessor();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                triggerUpdateProcessor();
            }
        };
    }

    private void triggerUpdateProcessor() {
        notifyDataSetChanged();
        mNativeVideoController.refreshAdPlacement(mOriginAdapter.getItemCount());
    }

    /**
     * Clean resources.
     */
    public void destroy() {
        mNativeVideoController.destroy();
    }

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
     *
     * @param appKey   - app key
     * @param position - position in list
     */
    public void putAdWithAppKeyToPosition(String appKey, int position) {
        if (position < 0) {
            mNativeVideoController.putAdWithAppKeyToPosition(appKey, 0);
        } else
            mNativeVideoController.putAdWithAppKeyToPosition(appKey, Math.min(position, mOriginAdapter.getItemCount()));
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
     *
     * @param binder - ViewBinder
     */
    public void setViewBinder(NativeVideoBinder binder) {
        if (mNativeVideoController != null) {
            mNativeVideoController.setViewBinder(binder);
        }
    }

    /**
     * Set listener to receive notifications during loading/showing process
     *
     * @param listener - listener
     */
    public void setListener(LoopMeBanner.Listener listener) {
        mNativeVideoController.setListener(listener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_AD) {
            return new NativeVideoViewHolder(initLayout(viewGroup));
        } else {
            return mOriginAdapter.onCreateViewHolder(viewGroup, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (isAd(position)) {
            Logging.out(LOG_TAG, "onBindViewHolder");
            configureAdView(viewHolder, position);
        } else {
            int initPosition = mNativeVideoController.getInitialPosition(position);
            mOriginAdapter.onBindViewHolder(viewHolder, initPosition);
        }
    }

    @Override
    public boolean isAd(int i) {
        return mNativeVideoController.getNativeVideoAd(i) != null;
    }

    @Override
    public int getItemViewType(int position) {
        int initPosition = mNativeVideoController.getInitialPosition(position);
        return isAd(position) ? TYPE_AD : mOriginAdapter.getItemViewType(initPosition);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        mOriginAdapter.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        if (isAd(position)) {
            return -System.identityHashCode(mNativeVideoController.getNativeVideoAd(position));
        } else {
            int initPosition = mNativeVideoController.getInitialPosition(position);
            return mOriginAdapter.getItemId(initPosition);
        }
    }

    @Override
    public int getItemCount() {
        return mOriginAdapter.getItemCount() + mNativeVideoController.getAdsCount();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        mOriginAdapter.onViewRecycled(holder);
    }

    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return mOriginAdapter.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mOriginAdapter.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        mOriginAdapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        mOriginAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        mOriginAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mOriginAdapter.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mOriginAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onDataSetChanged() {
        mOriginAdapter.notifyDataSetChanged();
    }

    private void configureAdView(RecyclerView.ViewHolder viewHolder, int position) {
        View rowView = mNativeVideoController.getAdView(mInflater, position);
        cleanView(viewHolder, rowView);
        ((NativeVideoViewHolder) viewHolder).addView(rowView);
        setNewLayoutParams(rowView);
    }

    private void setNewLayoutParams(View rowView) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rowView.getLayoutParams();
        layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        rowView.setLayoutParams(layoutParams);
    }

    private void cleanView(RecyclerView.ViewHolder viewHolder, View rowView) {
        if (viewHolder == null || rowView == null) {
            return;
        }
        ((NativeVideoViewHolder) viewHolder).removeAllViews();
        if (rowView.getParent() != null) {
            ((ViewGroup) rowView.getParent()).removeView(rowView);
        }
    }

    private View initLayout(ViewGroup viewGroup) {
        RelativeLayout layout = new RelativeLayout(viewGroup.getContext());
        layout.setLayoutParams(initParams());
        return layout;
    }

    private ViewGroup.LayoutParams initParams() {
        return new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected class NativeVideoViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout adView;

        private NativeVideoViewHolder(View view) {
            super(view);
            adView = (RelativeLayout) view;
        }

        private void addView(View view) {
            adView.addView(view);
        }

        public View getView() {
            return adView;
        }

        private void removeAllViews() {
            adView.removeAllViews();
        }
    }
}
