package com.loopme.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.MinimizedMode;
import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;

public class UiUtils {

    private static final String LOG_TAG = UiUtils.class.getSimpleName();
    private static final AdSpotDimensions DEFAULT_DIMENSIONS = new AdSpotDimensions(0, 0);

    @SuppressLint("NewApi")
    private static void addBordersToView(FrameLayout bannerView) {
        ShapeDrawable drawable = new ShapeDrawable(new RectShape());
        drawable.getPaint().setColor(Color.BLACK);
        drawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        drawable.getPaint().setAntiAlias(true);
        bannerView.setPadding(2, 2, 2, 2);
        bannerView.setBackground(drawable);
    }

    public static void configMinimizedViewLayoutParams(FrameLayout bannerView, MinimizedMode minimizedMode) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) bannerView.getLayoutParams();
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.bottomMargin = minimizedMode.getMarginBottom();
        lp.rightMargin = minimizedMode.getMarginRight();
        bannerView.setLayoutParams(lp);
    }

    public static AdSpotDimensions getViewSize(MinimizedMode minimizedMode, LoopMeAd loopMeAd, Constants.DisplayMode displayMode) {
        switch (displayMode) {
            case MINIMIZED: return getMinimizedViewSize(minimizedMode);
            case NORMAL: return getNormalViewSize(loopMeAd);
            case FULLSCREEN: return getFullScreenViewSize();
            default: throw new IllegalArgumentException();
        }
    }

    private static AdSpotDimensions getFullScreenViewSize() {
        return new AdSpotDimensions(Utils.getScreenWidth(), Utils.getScreenHeight());
    }

    private static AdSpotDimensions getNormalViewSize(LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            return loopMeAd.getAdSpotDimensions();
        }
        Logging.out(LOG_TAG, "WARNING: LoopMeAd is null");
        return DEFAULT_DIMENSIONS;
    }

    private static AdSpotDimensions getMinimizedViewSize(MinimizedMode minimizedMode) {
        if (minimizedMode != null) {
            return new AdSpotDimensions(minimizedMode.getWidth(), minimizedMode.getHeight());
        }
        Logging.out(LOG_TAG, "WARNING: MinimizedMode is null");
        return DEFAULT_DIMENSIONS;
    }

    public static FrameLayout createFrameLayout(Context context, AdSpotDimensions dimensions) {
        if (context == null || dimensions == null) {
            return null;
        }
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
            dimensions.getWidth(), dimensions.getHeight()
        ));
        addBordersToView(frameLayout);
        return frameLayout;
    }

    public static void broadcastIntent(Context context, String intentAction) {
        broadcastIntent(context, intentAction, Constants.DEFAULT_AD_ID);
    }

    public static void broadcastIntent(Context context, String intentAction, int adId) {
        if (context == null || TextUtils.isEmpty(intentAction)) {
            return;
        }
        Intent intent = new Intent(intentAction);
        intent.setPackage(context.getPackageName());
        if (adId != Constants.DEFAULT_AD_ID) {
            intent.putExtra(Constants.AD_ID_TAG, adId);
        }
        context.sendBroadcast(intent);
    }
}
