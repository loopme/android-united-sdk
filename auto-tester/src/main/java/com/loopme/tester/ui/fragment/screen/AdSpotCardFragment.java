package com.loopme.tester.ui.fragment.screen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopme.tester.R;
import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.enums.AdType;
import com.loopme.tester.enums.ViewMode;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.ui.activity.BaseActivity;
import com.loopme.tester.ui.activity.MainActivity;
import com.loopme.tester.ui.fragment.ActionBarFragment;
import com.loopme.tester.ui.fragment.BaseFragment;
import com.loopme.tester.ui.view.AutoView;
import com.loopme.tester.ui.view.ManualView;
import com.loopme.tester.utils.StringUtils;
import com.loopme.tester.utils.UiUtils;

/**
 * Created by katerina on 2/12/17.
 */

public class AdSpotCardFragment extends BaseFragment {

    private static final String ARG_AD_SPOT = "ARG_AD_SPOT";
    private boolean mManualViewMode = true;
    private View mManualLayout;
    private View mAutoLayout;
    private AdSpot mCurrentAdSpot;
    private RadioGroup mTestGroup;
    private BaseActivity mActivity;
    private ManualView mManualView;
    private AutoView mAutoView;

    private OnAdSpotCardFragmentListener mOnAdSpotCardFragmentListener;
    private OnAdSpotUpdateCallback mOnAdSpotUpdateCallback;
    private ActionBarFragment mActionBarFragment;
    private TextView mAppKeyTextView;
    private ImageView mAdSpotCardImageView;
    private TextView mAdTypeTextView;
    private TextView mBaseUrlTextView;

    public static AdSpotCardFragment newInstance(Bundle args) {
        AdSpotCardFragment adSpotCardFragment = new AdSpotCardFragment();
        adSpotCardFragment.setArguments(args);
        return adSpotCardFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAdSpotCardFragmentListener && context instanceof BaseActivity) {
            mActivity = (BaseActivity) context;
            mOnAdSpotCardFragmentListener = (OnAdSpotCardFragmentListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adspot_card, container, false);
        mOnAdSpotUpdateCallback = initAdSpotUpdateCallback();
        if (getArguments() != null) {
            setCurrentAdSpot(getArguments());
        }
        return view;
    }

    private void setCurrentAdSpot(Bundle arguments) {
        mCurrentAdSpot = arguments.getParcelable(ARG_AD_SPOT);
        if (mCurrentAdSpot != null) {
            findAdSpotById(mCurrentAdSpot.getAdSpotId(), mOnAdSpotUpdateCallback);
        }
    }

    private void findAdSpotById(long adSpotId, OnAdSpotUpdateCallback callback) {
        if (mActivity != null) {
            mActivity.findAdSpotById(adSpotId, callback);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mManualLayout = view.findViewById(R.id.manual_view_relativelayout);
        mAutoLayout = view.findViewById(R.id.auto_relativelayout);
        mManualLayout.setVisibility(View.VISIBLE);
        mAutoLayout.setVisibility(View.GONE);
        initActionBar();
        initViews(view);
        setViewsValues();
        initTestTypesViews(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        onResumeAd();
    }

    @Override
    public void onPause() {
        onPauseAd();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        updateAdSpotTime();
        mOnAdSpotCardFragmentListener = null;
        mManualView.onDestroy();
        mAutoView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mOnAdSpotCardFragmentListener = null;
        super.onDetach();
    }

    private void onResumeAd() {
        if (mManualViewMode) {
            mManualView.onResume();
        } else {
            mAutoView.onResume();
        }
    }

    private void onPauseAd() {
        if (mManualViewMode) {
//            mManualView.onPause();
        } else {
            mAutoView.onPause();
        }
    }

    private void initTestTypesViews(View view) {
        mManualView = new ManualView(view, mCurrentAdSpot, mActivity);
        mAutoView = new AutoView(view, mCurrentAdSpot, mActivity);
        mManualView.initView();
        mAutoView.initView();
    }

    private void initActionBar() {
        if (!areChildFragmentsAdded()) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            mActionBarFragment = ActionBarFragment.newInstance(ViewMode.VIEW, mCurrentAdSpot);
            addChildFragment(R.id.actionbar_root, mActionBarFragment, fragmentTransaction);
            makeTransaction(fragmentTransaction);
        }
    }

    private void initViews(View view) {
        mAppKeyTextView = (TextView) view.findViewById(R.id.fragment_adspot_card_appkey_text);
        mAdSpotCardImageView = (ImageView) view.findViewById(R.id.fragment_adspot_card_sdk_icon);
        mAdTypeTextView = (TextView) view.findViewById(R.id.fragment_adspot_card_ad_type_text);
        mBaseUrlTextView = (TextView) view.findViewById(R.id.fragment_adspot_card_ad_baseurl);
        mTestGroup = (RadioGroup) view.findViewById(R.id.fragment_adspot_card_adtype_radiogroup);

        mTestGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.fragment_adspot_card_manual_radio) {
                setManualView(true);
            } else {
                setManualView(false);
            }
        }
    };

    private void setManualView(boolean manualViewMode) {
        if (manualViewMode) {
            mManualViewMode = true;
            mManualLayout.setVisibility(View.VISIBLE);
            mAutoLayout.setVisibility(View.GONE);
            mAutoView.onDestroy();
        } else {
            mManualViewMode = false;
            mManualLayout.setVisibility(View.GONE);
            mAutoLayout.setVisibility(View.VISIBLE);
            mManualView.onDestroy();
        }
    }

    public void onEditAdSpot() {
        if (mCurrentAdSpot != null) {
            if (mOnAdSpotCardFragmentListener != null) {
                mOnAdSpotCardFragmentListener.onEditAdSpot(mCurrentAdSpot);
            }
        }
    }

    public static Bundle createArguments(AdSpot adSpot) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_AD_SPOT, adSpot);
        return args;
    }


    @Override
    public Bundle getStateBundle() {
        Bundle savedState = new Bundle();
        if (mCurrentAdSpot != null) {
            savedState.putParcelable(ARG_AD_SPOT, mCurrentAdSpot);
            return savedState;
        } else {
            return null;
        }
    }

    public void cancelTasks() {
        if (mAutoView != null) {
            mAutoView.cancelTask();
        }
    }

    public interface OnAdSpotCardFragmentListener {
        void onEditAdSpot(AdSpot adSpot);
    }

    @Override
    public void onBeforeClose() {
        if (mActivity != null && mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).restartLoader();
        }
    }

    private void updateAdSpotTime() {
        mCurrentAdSpot.setTime(System.currentTimeMillis());
        if (mActivity != null) {
            mActivity.updateAdSpot(mCurrentAdSpot);
        }
    }

    public void updateViews() {
        setViewsValues();
        updateActionBarTitle();
    }

    private void setViewsValues() {
        if (mCurrentAdSpot == null) {
            return;
        }
        AdType adType = mCurrentAdSpot.getType();
        AdSdk adSdk = mCurrentAdSpot.getSdk();

        mAppKeyTextView.setText(mCurrentAdSpot.getAppKey());
        mAdSpotCardImageView.setImageResource(UiUtils.getSdkTypeIcon(adSdk));
        mAdTypeTextView.setText(StringUtils.capitalizeFirstLetter(adType.toString()));

        if (adSdk == AdSdk.LOOPME || adSdk == AdSdk.LMVPAID) {
            mBaseUrlTextView.setText(mCurrentAdSpot.getBaseUrl());
        }
        mTestGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    private void updateActionBarTitle() {
        if (mActionBarFragment != null) {
            mActionBarFragment.setTitle(mCurrentAdSpot.getName());
        }
    }

    public interface OnAdSpotUpdateCallback {
        void onAdSpotUpdate(AdSpot adSpot);
    }

    private OnAdSpotUpdateCallback initAdSpotUpdateCallback() {
        return new OnAdSpotUpdateCallback() {
            @Override
            public void onAdSpotUpdate(AdSpot adSpot) {
                setUpdatedAdSpot(adSpot);
                updateViews();
                mManualView.startLoadAd();
            }
        };
    }

    private void setUpdatedAdSpot(AdSpot adSpot) {
        mCurrentAdSpot = adSpot;
        mManualView.setAdSpot(mCurrentAdSpot);
        mAutoView.setAdSpot(mCurrentAdSpot);
    }
}
