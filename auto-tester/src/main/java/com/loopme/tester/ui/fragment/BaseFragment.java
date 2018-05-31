package com.loopme.tester.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.loopme.tester.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by katerina on 2/9/17.
 */

public class BaseFragment extends Fragment {

    private boolean mChildFragmentsCreated;
    private boolean mHasLandscapeView;
    private List<ChildFragmentModel> mChildFragments = new ArrayList<>();
    private View mRootView;

    private void setActivity(Activity activity) {

    }

    public void setHasLandscapeView(boolean value) {
        mHasLandscapeView = value;
    }

    public boolean hasLandscapeView() {
        return mHasLandscapeView;
    }

    protected List<ChildFragmentModel> getChildFragments() {
        List<ChildFragmentModel> childFragmentModelList = new ArrayList<>();
        childFragmentModelList.addAll(mChildFragments);
        return childFragmentModelList;
    }

    protected void makeTransaction(Runnable runnable) {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            baseActivity.makeTransaction(runnable);
        }
    }

    protected void makeTransaction(FragmentTransaction fragmentTransaction) {
        if (fragmentTransaction != null) {
            BaseActivity baseActivity = (BaseActivity) getActivity();
            if (baseActivity != null) {
                baseActivity.makeTransaction(fragmentTransaction);
            }
        }
    }

    protected void removeRunnable(Runnable runnable) {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            baseActivity.removeRunnable(runnable);
        }
    }

    protected void postRunnable(Runnable runnable) {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            baseActivity.postRunnable(runnable);
        }
    }

    protected void postRunnableDelayed(Runnable runnable, long delayed) {
        BaseActivity baseActivity = (BaseActivity) getActivity();
        if (baseActivity != null) {
            baseActivity.postRunnableDelayed(runnable, delayed);
        }
    }

    public Bundle getStateBundle() {
        return null;
    }

    public void addChildFragment(int resourceId, BaseFragment fragment) {
        addChildFragment(resourceId, fragment, null);
    }

    public void addChildFragment(int resourceId, BaseFragment fragment, FragmentTransaction fragmentTransaction) {
        mChildFragmentsCreated = true;
        ChildFragmentModel childFragmentModel = new ChildFragmentModel(resourceId, fragment);
        mChildFragments.add(childFragmentModel);

        if (fragmentTransaction != null) {
            fragmentTransaction.add(resourceId, fragment);
        } else {
            FragmentTransaction newFragmentTransaction = getFragmentManager().beginTransaction();
            newFragmentTransaction.add(resourceId, fragment);
            makeTransaction(newFragmentTransaction);
        }
    }

    public void replaceChildFragment(int resourceId, BaseFragment fragment, FragmentTransaction fragmentTransaction) {
        mChildFragmentsCreated = true;
        ChildFragmentModel childFragmentModel = new ChildFragmentModel(resourceId, fragment);
        mChildFragments.add(childFragmentModel);

        if (fragmentTransaction != null) {
            fragmentTransaction.replace(resourceId, fragment);
        } else {
            FragmentTransaction fragmentTransaction2 = getFragmentManager().beginTransaction();
            fragmentTransaction2.replace(resourceId, fragment);
            makeTransaction(fragmentTransaction2);
        }
    }

    public void onOrientationChanged() {
        FragmentManager fm = getFragmentManager();
        for (ChildFragmentModel childFragmentModel : mChildFragments) {

            fm.beginTransaction()
                    .remove(childFragmentModel.getChildFragment())
                    .commitNow();

            fm.beginTransaction()
                    .add(childFragmentModel.getResourceId(), childFragmentModel.getChildFragment())
                    .commitNow();
            childFragmentModel.getChildFragment().onOrientationChanged();
        }
    }

    public boolean areChildFragmentsAdded() {
        return mChildFragmentsCreated;
    }

    protected void setChildFragmentAdded(boolean value) {
        mChildFragmentsCreated = value;
    }

    protected class ChildFragmentModel {
        private BaseFragment mChildFragment;
        private int mResourceId;

        private ChildFragmentModel(int resourceId, BaseFragment childFragment) {
            mChildFragment = childFragment;
            mResourceId = resourceId;
        }

        public BaseFragment getChildFragment() {
            return mChildFragment;
        }

        public int getResourceId() {
            return mResourceId;
        }
    }

    public void openScreen(int screenId) {
        ((BaseActivity) getActivity()).openScreen(screenId);
    }

    public boolean processBackPress() {
        return false;
    }

    public void onBeforeClose() {

    }

    public void onBeforeOrientationChange() {
        for (ChildFragmentModel childFragmentModel : mChildFragments) {
            childFragmentModel.getChildFragment().onBeforeOrientationChange();
        }
    }

    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
    }



    public void setRootView(View mRootView) {
        this.mRootView = mRootView;
        setRootVisible(true);
    }


    private void setRootVisible(boolean visible) {
        if (mRootView != null) {
            if (visible) {
                mRootView.setVisibility(View.VISIBLE);
            } else {
                mRootView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        setRootVisible(false);
        super.onDestroyView();
    }
}
