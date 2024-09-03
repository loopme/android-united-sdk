package com.loopme;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ViewAbilityUtils {
    private static final String LOG_TAG = ViewAbilityUtils.class.getSimpleName();
    private static final Handler mHandler = new Handler(Looper.getMainLooper());
    private static final double TOTAL_OVERLAPPED = 1.0D;
    private static final double INVISIBLE = 0;
    private static final double FIFTY_PERCENT = 0.5;
    private static final long DELAY = 100;

    private ViewAbilityUtils() {
    }

    public static void calculateViewAbilitySyncDelayed(final View checkedView, final OnResultListener listener) {
        mHandler.postDelayed(() -> calculateViewAbilitySync(checkedView, listener), DELAY);
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
        if (isViewInFocus(view) && isViewShown(view) && getSquareOfView(view) > 0) {
            return calculateViewAbilityInfoInternal(view);
        } else {
            Logging.out(LOG_TAG, "view != null " + (view != null));
            Logging.out(LOG_TAG, "isViewInFocus " + isViewInFocus(view));
            Logging.out(LOG_TAG, "isViewShown(view) " + isViewShown(view));
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

    private static boolean isViewShown(View view) {
        return view != null && view.isShown();
    }

    private static double getSquareOfView(View view) {
        Rect rect = getViewRect(view);
        return getRectSquare(rect);
    }

    private static Rect getViewRect(View view) {
        if (view == null)
            return new Rect(0, 0, 0, 0);

        int[] locationInWindow = new int[]{0, 0};
        view.getLocationInWindow(locationInWindow);

        int left = locationInWindow[0];
        int top = locationInWindow[1];

        return new Rect(
                left,
                top,
                left + view.getWidth(),
                top + view.getHeight());
    }

    private static double getRectSquare(Rect rect) {
        return rect == null ? 0 : rect.width() * rect.height();
    }

    private static ViewAbilityInfo calculateViewAbilityInfoInternal(View checkedView) {
        ViewAbilityInfo viewabilityInfo = new ViewAbilityInfo();
        Rect globalRect = new Rect(0, 0, 0, 0);

        if (!checkedView.getGlobalVisibleRect(globalRect))
            return viewabilityInfo;

        double totalViewSquare = getRectSquare(globalRect);
        View rootView = checkedView.getRootView();

        if (!(rootView instanceof ViewGroup))
            return viewabilityInfo;

        HashSet<Rect> hashSet = new HashSet<>();

        if (isTotalOverlapped(globalRect, checkedView, hashSet)) {
            viewabilityInfo.setOverlapping(TOTAL_OVERLAPPED);
            viewabilityInfo.setVisibility(INVISIBLE);
            return viewabilityInfo;
        }

        double overlappedViewSquare = calculateOverlappedSquare(globalRect, hashSet);
        if (overlappedViewSquare > 0) {
            double overlappedPercent = overlappedViewSquare / totalViewSquare;
            viewabilityInfo.setOverlapping(overlappedPercent);
        }

        double visibleViewSquare = totalViewSquare - overlappedViewSquare;
        double visibility = visibleViewSquare / getSquareOfView(checkedView);
        viewabilityInfo.setVisibility(visibility);

        return viewabilityInfo;
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

    @NonNull
    private static ArrayList<Rect> createChildrenRectSortedArrayList(Set<Rect> rectSet) {
        ArrayList<Rect> childRectList = new ArrayList<>(rectSet);
        Collections.sort(childRectList, (o1, o2) -> Integer.compare(o1.top, o2.top));
        return childRectList;
    }

    private static boolean isTotalOverlapped(Rect rootRect, View checkedView, Set<Rect> rectSet) {
        View rootView = checkedView.getRootView();
        ArrayDeque<View> arrayDeque = new ArrayDeque<>();
        arrayDeque.add(rootView);

        int counter = 0;
        boolean checkedViewFoundInViewTree = false;

        while (!arrayDeque.isEmpty() && counter < 250) {
            counter++;
            View lastView = arrayDeque.pollLast();

            if (lastView.equals(checkedView)) {
                checkedViewFoundInViewTree = true;
                continue;
            }

            if (!isViewVisible(lastView))
                continue;

            if (lastView instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) lastView;
                if (!(lastView instanceof ListView)) {
                    int childCount = viewGroup.getChildCount();
                    for (int i = childCount - 1; i >= 0; i--) {
                        View childView = viewGroup.getChildAt(i);
                        if (!isEmptyView(childView))
                            arrayDeque.add(childView);
                    }
                }
            }

            if (mayOverlap(lastView, checkedView, checkedViewFoundInViewTree)) {
                Rect viewRect = getViewRect(lastView);
                if (viewRect.setIntersect(rootRect, viewRect)) {
                    rectSet.add(viewRect);
                    if (viewRect.contains(rootRect))
                        return true;
                }
            }
        }

        return false;
    }

    // TODO.
    private static boolean isEmptyView(View view) {
        return view == null ||
                view instanceof FrameLayout &&
                        view.getClass() == FrameLayout.class &&
                        ((FrameLayout) view).getChildCount() == 0 &&
                        view.getBackground() == null;
    }

    private static boolean mayOverlap(View lastView, View checkedView, boolean checkedViewFound) {
        float lvElev = lastView.getElevation();
        float cvElev = checkedView.getElevation();

        return lvElev > cvElev || checkedViewFound;
    }

    public static class ViewAbilityInfo {
        private double mVisibility;

        public double getVisibility() {
            return mVisibility;
        }

        public void setVisibility(double mVisibility) {
            this.mVisibility = mVisibility;
            Logging.out(LOG_TAG, "visibility : " + ((int) (mVisibility * 100)) + "%");
        }

        public void setOverlapping(double mOverlapping) {
            Logging.out(LOG_TAG, "overlapping : " + ((int) (mOverlapping * 100)) + "%");
        }

        public boolean isVisibleMore50Percents() {
            return mVisibility > FIFTY_PERCENT;
        }

    }

    public interface OnResultListener {
        void onResult(ViewAbilityInfo info);
    }
}