package com.loopme.tester.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopme.tester.R;
import com.loopme.tester.enums.ViewMode;
import com.loopme.tester.model.AdSpot;


/**
 * Created by katerina on 2/12/17.
 */

public class ActionBarFragment extends BaseFragment implements
        View.OnClickListener {

    private final static String ARG_VIEW_MODE = "ARG_VIEW_MODE";
    private final static String ARG_AD_SPOT = "ARG_AD_SPOT";
    private OnActionBarFragmentListener mOnActionBarFragmentListener;
    private RelativeLayout mInfoAndAddButtonsLayout;
    private RelativeLayout mCancelAndSaveButtonsLayout;
    private RelativeLayout mEditBarLayout;
    private ViewMode mViewMode;
    private AdSpot mAdSpot;
    private TextView mTitleTextView;

    public ActionBarFragment() {
    }

    public static ActionBarFragment newInstance(ViewMode viewMode) {
        ActionBarFragment fragment = new ActionBarFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VIEW_MODE, viewMode);
        fragment.setArguments(args);
        return fragment;
    }

    public static ActionBarFragment newInstance(ViewMode viewMode, AdSpot adSpot) {
        ActionBarFragment fragment = new ActionBarFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VIEW_MODE, viewMode);
        args.putParcelable(ARG_AD_SPOT, adSpot);
        fragment.setArguments(args);
        return fragment;
    }

    public static ActionBarFragment newInstance() {
        return new ActionBarFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.actionbar_main_view, container, false);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mViewMode = (ViewMode) getArguments().getSerializable(ARG_VIEW_MODE);
                mAdSpot = getArguments().getParcelable(ARG_AD_SPOT);
            }
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInfoAndAddButtonsLayout = (RelativeLayout) view.findViewById(R.id.actionbar_info_and_add_buttons_layout);
        mCancelAndSaveButtonsLayout = (RelativeLayout) view.findViewById(R.id.actionbar_cancel_and_save_buttons_layout);
        mEditBarLayout = (RelativeLayout) view.findViewById(R.id.actionbar_edit_adspot_layout);

        view.findViewById(R.id.action_bar_cancel_button).setOnClickListener(this);
        view.findViewById(R.id.action_bar_save_button).setOnClickListener(this);
        view.findViewById(R.id.action_bar_info_button).setOnClickListener(this);
        view.findViewById(R.id.action_bar_add_button).setOnClickListener(this);
        view.findViewById(R.id.action_bar_back_to_adspots).setOnClickListener(this);
        view.findViewById(R.id.action_bar_edit_button).setOnClickListener(this);
        mTitleTextView = (TextView) view.findViewById(R.id.action_bar_title);

        if (mViewMode != null) {
            setViewMode(mViewMode);
        } else {
            setViewMode(ViewMode.INFO);
        }
    }

    private void setViewMode(ViewMode viewMode) {
        switch (viewMode) {
            case EDIT: {
                mEditBarLayout.setVisibility(View.GONE);
                mInfoAndAddButtonsLayout.setVisibility(View.GONE);
                mCancelAndSaveButtonsLayout.setVisibility(View.VISIBLE);
                setTitle(mAdSpot);
                break;
            }
            case CREATE: {
                mEditBarLayout.setVisibility(View.GONE);
                mInfoAndAddButtonsLayout.setVisibility(View.GONE);
                mCancelAndSaveButtonsLayout.setVisibility(View.VISIBLE);
                setTitle(getString(R.string.new_ad_spot));
                break;
            }
            case VIEW: {
                mEditBarLayout.setVisibility(View.VISIBLE);
                mInfoAndAddButtonsLayout.setVisibility(View.GONE);
                mCancelAndSaveButtonsLayout.setVisibility(View.GONE);
                setTitle(mAdSpot);
                break;
            }
            case INFO: {
                mEditBarLayout.setVisibility(View.GONE);
                mInfoAndAddButtonsLayout.setVisibility(View.VISIBLE);
                mCancelAndSaveButtonsLayout.setVisibility(View.GONE);
                setTitle(getString(R.string.ad_spots));
                break;
            }
        }
    }

    private void setTitle(AdSpot adSpot) {
        if (adSpot != null) {
            setTitle(adSpot.getName());
        }
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitleTextView.setText(title);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnActionBarFragmentListener = (OnActionBarFragmentListener) context;
    }

    @Override
    public void onDetach() {
        mOnActionBarFragmentListener = null;
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_bar_info_button: {
                openInfoFragment();
                break;
            }
            case R.id.action_bar_add_button: {
                openAddSpotFragment();
                break;
            }
            case R.id.action_bar_cancel_button: {
                onCancel();
                break;
            }
            case R.id.action_bar_save_button: {
                onSave();
                break;
            }
            case R.id.action_bar_back_to_adspots: {
                onCancel();
                break;
            }
            case R.id.action_bar_edit_button: {
                openEditAdSpotFragment();
                break;
            }
        }
    }

    private void openEditAdSpotFragment() {
        if (mOnActionBarFragmentListener != null) {
            mOnActionBarFragmentListener.editAdSpot();
        }
    }

    private void onCancel() {

        if (mOnActionBarFragmentListener != null) {
            mOnActionBarFragmentListener.onClose();
        }
    }

    private void onSave() {
        if (mOnActionBarFragmentListener != null) {
            mOnActionBarFragmentListener.onSave();
        }
    }

    private void openAddSpotFragment() {
        if (mOnActionBarFragmentListener != null) {
            mOnActionBarFragmentListener.onNewAdSpot();
        }
    }

    private void openInfoFragment() {
        if (mOnActionBarFragmentListener != null) {
            mOnActionBarFragmentListener.onOpenInfo();
        }
    }

    public interface OnActionBarFragmentListener {
        void onNewAdSpot();

        void onOpenInfo();

        void onSave();

        void onClose();

        void editAdSpot();
    }


}
