package com.loopme;

import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class MoatViewAbilityUtils {
    private static final String LOG_TAG = MoatViewAbilityUtils.class.getSimpleName();
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static final double TOTAL_OVERLAPPED = 1.0D;
    private static final double INVISIBLE = 0;
    private static final double FIFTY_PERCENT = 0.5;
    private static final long DELAY = 100;

    private MoatViewAbilityUtils() {
    }

    public static void calculateViewAbilitySyncDelayed(final View checkedView, final OnResultListener listener) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                calculateViewAbilitySync(checkedView, listener);
            }
        }, DELAY);
    }

    private static void calculateViewAbilitySync(final View view, final OnResultListener listener) {
        long start = System.currentTimeMillis();
        ViewAbilityInfo info = calculateViewAbilityInfo(view);
        long time = System.currentTimeMillis() - start;
        Logging.out(LOG_TAG, "time to calculate " + time + " mills");
        if (listener != null) {
            listener.onResult(info);
        }
    }

    public static ViewAbilityInfo calculateViewAbilityInfo(View view) {
        if (view != null && isViewInFocus(view) && !isViewHidden(view) && getSquareOfView(view) > 0) {
            return calculateViewAbilityInfoInternal(view);
        } else {
            Logging.out(LOG_TAG, "view != null " + (view != null));
            Logging.out(LOG_TAG, "isViewInFocus " + isViewInFocus(view));
            Logging.out(LOG_TAG, "isViewNotHidden(view) " + !isViewHidden(view));
            Logging.out(LOG_TAG, "getSquareOfView(view) > 0 " + (getSquareOfView(view) > 0));
            return new ViewAbilityInfo();
        }
    }

    private static boolean isViewVisible(View view) {
        return view != null && view.isShown() && (double) view.getAlpha() > 0.0;
    }

    private static boolean isViewInFocus(View view) {
        return view != null && view.hasWindowFocus();
    }

    private static boolean isViewHidden(View view) {
        return view == null || !view.isShown();
    }

    private static double getSquareOfView(View view) {
        Rect rect = getViewRect(view);
        return getRectSquare(rect);
    }

    private static Rect getViewRect(View view) {
        int[] locationInWindowArray = new int[]{0, 0};
        if (view != null) {
            view.getLocationInWindow(locationInWindowArray);
            int left = locationInWindowArray[0];
            int top = locationInWindowArray[1];
            int right = left + view.getWidth();
            int bottom = top + view.getHeight();
            return new Rect(left, top, right, bottom);
        } else {
            return new Rect(0, 0, 0, 0);
        }
    }

    private static double getRectSquare(Rect rect) {
        return rect == null ? 0 : rect.width() * rect.height();
    }

    private static ViewAbilityInfo calculateViewAbilityInfoInternal(View checkedView) {
        ViewAbilityInfo viewAbilityInfo = new ViewAbilityInfo();
        Rect globalRect = new Rect(0, 0, 0, 0);

        if (checkedView.getGlobalVisibleRect(globalRect)) {
            double totalViewSquare = getRectSquare(globalRect);
            View rootView = checkedView.getRootView();

            if (rootView instanceof ViewGroup) {
                HashSet<Rect> hashSet = new HashSet<>();
                boolean totalOverlapped = isTotalOverlapped(globalRect, checkedView, hashSet);

                if (totalOverlapped) {
                    viewAbilityInfo.setOverlapping(TOTAL_OVERLAPPED);
                    viewAbilityInfo.setVisibility(INVISIBLE);
                    return viewAbilityInfo;
                } else {
                    double overlappedViewSquare = calculateOverlappedSquare(globalRect, hashSet);
                    if (overlappedViewSquare > 0) {
                        double overlappedPercent = overlappedViewSquare / totalViewSquare;
                        viewAbilityInfo.setOverlapping(overlappedPercent);
                    }

                    double visibleViewSquare = totalViewSquare - overlappedViewSquare;
                    double visibility = visibleViewSquare / getSquareOfView(checkedView);
                    viewAbilityInfo.setVisibility(visibility);
                }
            }
        }

        return viewAbilityInfo;
    }

    private static int calculateOverlappedSquare(Rect rootRect, Set<Rect> rectSet) {
        int overlapping = 0;
        if (rectSet.isEmpty()) {
            return overlapping;
        }
        ArrayList<Rect> childRectList = createChildrenRectSortedArrayList(rectSet);

        ArrayList<Integer> sidesListOfChildRect = createSidesListOfChildRect(childRectList);

        for (int i = 0; i < sidesListOfChildRect.size() - 1; ++i) {
            if (!sidesListOfChildRect.get(i).equals(sidesListOfChildRect.get(i + 1))) {
                Rect tempRect = new Rect(sidesListOfChildRect.get(i), rootRect.top, sidesListOfChildRect.get(i + 1), rootRect.bottom);
                int rootRectTop = rootRect.top;

                for (Rect childRect : childRectList) {
                    if (Rect.intersects(childRect, tempRect)) {
                        if (childRect.bottom > rootRectTop) {
                            overlapping += tempRect.width() * (childRect.bottom - Math.max(rootRectTop, childRect.top));
                            rootRectTop = childRect.bottom;
                        }
                        if (childRect.bottom == tempRect.bottom) {
                            break;
                        }
                    }
                }
            }
        }

        return overlapping;
    }

    private static ArrayList<Integer> createSidesListOfChildRect(ArrayList<Rect> childRectList) {
        ArrayList<Integer> list = new ArrayList<>();

        for (Rect rect : childRectList) {
            list.add(rect.left);
            list.add(rect.right);
        }

        Collections.sort(list);
        return list;
    }

    private static ArrayList<Rect> createChildrenRectSortedArrayList(Set<Rect> rectSet) {
        ArrayList<Rect> childRectList = new ArrayList<>();
        childRectList.addAll(rectSet);
        Collections.sort(childRectList, new Comparator<Rect>() {
            @Override
            public int compare(Rect rect1, Rect rect2) {
                return Integer.valueOf(rect1.top).compareTo(rect2.top);
            }
        });
        return childRectList;
    }


    private static boolean isTotalOverlapped(Rect rootRect, View checkedView, Set<Rect> rectSet) {
        int counter = 0;
        boolean var4 = false;
        View rootView = checkedView.getRootView();
        ArrayDeque<View> arrayDeque = new ArrayDeque<>();
        arrayDeque.add(rootView);

        while (!arrayDeque.isEmpty() && counter < 250) {
            ++counter;
            View lastView = arrayDeque.pollLast();
            if (lastView.equals(checkedView)) {
                var4 = true;
            } else if (isViewVisible(lastView)) {
                if (lastView instanceof ViewGroup && !(lastView instanceof ListView)) {
                    ViewGroup viewGroup = (ViewGroup) lastView;
                    int childCount = viewGroup.getChildCount();

                    for (int i = childCount - 1; i >= 0; --i) {
                        View childView = viewGroup.getChildAt(i);
                        if (!isEmptyView(childView)) {
                            arrayDeque.add(childView);
                        }
                    }
                }

                boolean sdkValidation = checkSdk21(lastView, checkedView, var4);

                if (sdkValidation) {
                    Rect viewRect = getViewRect(lastView);
                    if (viewRect.setIntersect(rootRect, viewRect)) {
                        rectSet.add(viewRect);
                        if (viewRect.contains(rootRect)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static boolean isEmptyView(View view) {
        if (view == null) {
            return true;
        }
        if (view instanceof FrameLayout && view.getClass() == FrameLayout.class) {
            if (((FrameLayout) view).getChildCount() == 0 && view.getBackground() == null) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkSdk21(View lastView, View checkedView, boolean var4) {
        if (Build.VERSION.SDK_INT >= 21) {
            if (lastView.getElevation() > checkedView.getElevation()) {
                return true;
            } else if (var4 && lastView.getElevation() == checkedView.getElevation()) {
                return true;
            }
        }
        return var4;
    }

    public static class ViewAbilityInfo {
        private double mVisibility;
        private double mOverlapping;

        public double getVisibility() {
            return mVisibility;
        }

        public void setVisibility(double mVisibility) {
            this.mVisibility = mVisibility;
            Logging.out(LOG_TAG, "visibility : " + ((int) (mVisibility * 100)) + "%");
        }

        public double getOverlapping() {
            return mOverlapping;
        }

        public void setOverlapping(double mOverlapping) {
            this.mOverlapping = mOverlapping;
            Logging.out(LOG_TAG, "overlapping : " + ((int) (mOverlapping * 100)) + "%");
        }

        public boolean isVisibleMore50Percents() {
            return mVisibility > FIFTY_PERCENT;
        }

        public boolean isVisibleMore50Percents() {
            return mVisibility > FIFTY_PERCENT;
        }

    }

    public interface OnResultListener {
        void onResult(ViewAbilityInfo info);
    }

    public interface OnResultListener {
        void onResult(ViewAbilityInfo info);
    }
}