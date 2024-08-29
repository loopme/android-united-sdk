package com.loopme;

import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loopme.ad.AdSpotDimensions;
import com.loopme.utils.Utils;

public class MinimizedMode {

    private static final String LOG_TAG = MinimizedMode.class.getSimpleName();

    public static int MARGIN_RIGHT = 10;
    public static int MARGIN_BOTTOM = 10;

    private final AdSpotDimensions mMinimizedViewDims = new AdSpotDimensions(300, 100);
    @NonNull
    public AdSpotDimensions getDimensions() { return mMinimizedViewDims; }

    private final ViewGroup mRoot;
    private final RecyclerView mRecyclerView;

    private int mPosition;
    public void setPosition(int position) { mPosition = position; }

    public MinimizedMode(@NonNull ViewGroup root, @NonNull RecyclerView recyclerView) {
        mRoot = root;
        mRecyclerView = recyclerView;
        DisplayMetrics dm = Utils.getDisplayMetrics();
        boolean isPortrait = dm.heightPixels > dm.widthPixels;
        int width = isPortrait ? dm.widthPixels / 2 : dm.widthPixels / 3;
        int height = width * 2 / 3;
        mMinimizedViewDims.setDimensions(width, height);
    }

    public void addView(FrameLayout view) { mRoot.addView(view); }
    public void onViewClicked() { mRecyclerView.smoothScrollToPosition(mPosition); }
}
