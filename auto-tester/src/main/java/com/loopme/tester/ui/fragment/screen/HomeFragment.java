package com.loopme.tester.ui.fragment.screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopme.tester.R;
import com.loopme.tester.enums.ViewMode;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.ui.fragment.ActionBarFragment;
import com.loopme.tester.ui.fragment.ActiveSearchFragment;
import com.loopme.tester.ui.fragment.AdSpotListFragment;
import com.loopme.tester.ui.fragment.BaseFragment;
import com.loopme.tester.utils.UiUtils;

import java.util.List;

/**
 * Created by katerina on 2/12/17.
 */

public class HomeFragment extends BaseFragment {

    private View mActionbarViewRoot;
    private View mAdSpotCardViewRoot;
    private View mAdSpotsListViewRoot;
    private View mEditKeyViewRoot;
    private View mActiveSearchViewRoot;

    public HomeFragment() {
        setHasLandscapeView(true);
    }

    public static Fragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActionbarViewRoot = view.findViewById(R.id.actionbar_container);
        mAdSpotsListViewRoot = view.findViewById(R.id.ad_list_root);
        mAdSpotCardViewRoot = view.findViewById(R.id.ad_spot_card_root);
        mEditKeyViewRoot = view.findViewById(R.id.edit_root);
        mActiveSearchViewRoot = view.findViewById(R.id.active_search_root);

        initFragments();
    }

    private void initFragments() {
        if (!areChildFragmentsAdded()) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            addChildFragment(R.id.actionbar_container, ActionBarFragment.newInstance(ViewMode.INFO), fragmentTransaction);
            addChildFragment(R.id.ad_list_root, AdSpotListFragment.newInstance(), fragmentTransaction);
            addChildFragment(R.id.active_search_root, ActiveSearchFragment.newInstance(), fragmentTransaction);
            makeTransaction(fragmentTransaction);
        }
        showInitialFragment();
        UiUtils.hideSoftKeyboard(mActiveSearchViewRoot, getActivity());
    }

    public void showInitialFragment() {
        mEditKeyViewRoot.setVisibility(View.GONE);
        mAdSpotCardViewRoot.setVisibility(View.GONE);
        mActiveSearchViewRoot.setVisibility(View.VISIBLE);
        mAdSpotCardViewRoot.setVisibility(View.GONE);
        mAdSpotsListViewRoot.setVisibility(View.VISIBLE);
        mActionbarViewRoot.setVisibility(View.VISIBLE);
    }

    public void updateAdSpotList(List<AdSpot> adSpotList) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.ad_list_root);
        if (fragment != null && fragment instanceof AdSpotListFragment) {
            ((AdSpotListFragment) fragment).updateList(adSpotList);
        }
    }


    public void refreshAdSpotListView() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.ad_list_root);
        if (fragment instanceof AdSpotListFragment) {
            ((AdSpotListFragment) fragment).refreshView();
        }
    }

    public void requestFocus() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.ad_list_root);
        if (fragment instanceof AdSpotListFragment) {
            ((AdSpotListFragment) fragment).requestFocus();
        }
    }

    public void clearSearchText() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.active_search_root);
        if (fragment != null && fragment instanceof ActiveSearchFragment) {
            ((ActiveSearchFragment) fragment).disableSearch();
        }
    }
}
