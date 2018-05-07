package com.loopme.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

/**
 * Created by katerina on 5/7/18.
 */

public class AnimationUtils {
    public static Animation getAlphaAnimation(AnimationType type, final View view) {
        Animation.AnimationListener listener = getAnimationListener(type, view);
        switch (type) {
            case IN: {
                Animation animation = getAlphaAnimation(0f, 1.0f);
                animation.setAnimationListener(listener);
                return animation;
            }
            default: {
                Animation animation = getAlphaAnimation(1.0f, 0f);
                animation.setAnimationListener(listener);
                return animation;
            }
        }
    }


    private static Animation.AnimationListener getAnimationListener(final AnimationType type, final View view) {
        return new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (type == AnimationType.IN) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (type == AnimationType.OUT) {
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        };
    }

    private static Animation getAlphaAnimation(float from, float to) {
        Animation animation = new AlphaAnimation(from, to);
        animation.setDuration(300);
        animation.setFillAfter(true);
        return animation;
    }

    public enum AnimationType {
        IN, OUT,
    }
}