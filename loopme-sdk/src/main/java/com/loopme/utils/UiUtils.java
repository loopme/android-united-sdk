package com.loopme.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.loopme.Constants;
import com.loopme.Logging;
import com.loopme.MinimizedMode;
import com.loopme.ad.AdSpotDimensions;
import com.loopme.ad.LoopMeAd;
import com.loopme.models.BannerVisibility;
import com.loopme.views.activity.AdBrowserActivity;

/**
 * Created by vynnykiakiv on 6/26/17.
 */

public class UiUtils {

    private static final String LOG_TAG = UiUtils.class.getSimpleName();
    private static final AdSpotDimensions DEFAULT_DIMENSIONS = new AdSpotDimensions(0, 0);

    @SuppressLint("NewApi")
    public static void addBordersToView(FrameLayout bannerView) {
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

    public static Intent createRedirectIntent(String url, LoopMeAd loopMeAd) {
        if (TextUtils.isEmpty(url) || loopMeAd == null) {
            return new Intent();
        }
        Intent redirectIntent = new Intent(loopMeAd.getContext(), AdBrowserActivity.class);
        redirectIntent.putExtra(Constants.EXTRA_URL, url);
        redirectIntent.putExtra(Constants.AD_ID_TAG, loopMeAd.getAdId());
        redirectIntent.putExtra(Constants.FORMAT_TAG, loopMeAd.getAdFormat());
        redirectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return redirectIntent;
    }

    public static BannerVisibility ensureAdIsVisible(View view) {
        if (view == null) {
            return BannerVisibility.BANNER_INVISIBLE;
        }

        Rect rect = new Rect();
        boolean b = view.getGlobalVisibleRect(rect);

        int halfOfView = view.getHeight() / 2;
        int rectHeight = rect.height();

        if (b) {
            if (rectHeight < halfOfView) {
                return BannerVisibility.BANNER_HALF_VISIBLE;
            } else if (rectHeight >= halfOfView) {
                return BannerVisibility.BANNER_VISIBLE;
            }
        }
        return BannerVisibility.BANNER_INVISIBLE;
    }

    public static AdSpotDimensions getViewSize(MinimizedMode minimizedMode, LoopMeAd loopMeAd, Constants.DisplayMode displayMode) {
        switch (displayMode) {
            case MINIMIZED: {
                return getMinimizedViewSize(minimizedMode);
            }
            case NORMAL: {
                return getNormalViewSize(loopMeAd);
            }
            case FULLSCREEN: {
                return getFullScreenViewSize();
            }
        }
        return DEFAULT_DIMENSIONS;
    }

    private static AdSpotDimensions getFullScreenViewSize() {
        return new AdSpotDimensions(Utils.getScreenWidth(), Utils.getScreenHeight());
    }

    private static AdSpotDimensions getNormalViewSize(LoopMeAd loopMeAd) {
        if (loopMeAd != null) {
            return loopMeAd.getAdSpotDimensions();
        } else {
            Logging.out(LOG_TAG, "WARNING: LoopMeAd is null");
        }
        return DEFAULT_DIMENSIONS;
    }

    private static AdSpotDimensions getMinimizedViewSize(MinimizedMode minimizedMode) {
        if (minimizedMode != null) {
            return new AdSpotDimensions(minimizedMode.getWidth(), minimizedMode.getHeight());
        } else {
            Logging.out(LOG_TAG, "WARNING: MinimizedMode is null");
        }
        return DEFAULT_DIMENSIONS;
    }

    public static FrameLayout createFrameLayout(Context context, int width, int height) {
        if (context == null) {
            return null;
        }
        FrameLayout frameLayout = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        frameLayout.setLayoutParams(params);
        return frameLayout;
    }

    public static void broadcastIntent(Context context, String intentAction) {
        broadcastIntent(context, intentAction, Constants.DEFAULT_AD_ID);
    }

    public static void broadcastIntent(Context context, String intentAction, int adId) {
        if (context != null && !TextUtils.isEmpty(intentAction)) {
            Intent intent = new Intent();
            intent.setAction(intentAction);
            if (adId != Constants.DEFAULT_AD_ID) {
                intent.putExtra(Constants.AD_ID_TAG, adId);
            }
            context.sendBroadcast(intent);
        }
    }
}
