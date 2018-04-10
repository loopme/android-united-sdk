package com.loopme.tester.ui.fragment.screen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.loopme.tester.R;
import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.enums.AdType;
import com.loopme.tester.enums.ViewMode;
import com.loopme.tester.model.AdSpot;
import com.loopme.tester.ui.fragment.ActionBarFragment;
import com.loopme.tester.ui.fragment.AppkeyEditorFragment;
import com.loopme.tester.ui.fragment.BaseFragment;

public class EditAdSpotFragment extends BaseFragment {

    private static final String ARG_VIEW_MODE = "ARG_VIEW_MODE";
    public static final String ARG_AD_SPOT = "ARG_AD_SPOT";
    public static final String SHORT_APP_KEY = "test_mpu";

    private RadioGroup mSdkGroup;
    private RadioGroup mAdTypeGroup;
    private ViewMode mViewMode;
    private AdSpot mCurrentAdSpot;
    private Context mContext;
    private View mRootView;
    private OnEditAdFragmentListener mOnEditAdFragmentListener;
    private AdSpot mAdSpotToUpdate;

    public static EditAdSpotFragment newInstance(Bundle args) {
        EditAdSpotFragment editAdSpotFragment = new EditAdSpotFragment();
        editAdSpotFragment.setArguments(args);
        return editAdSpotFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_edit_adspot, container, false);
        if (getArguments() != null) {
            mViewMode = (ViewMode) getArguments().getSerializable(ARG_VIEW_MODE);
            mCurrentAdSpot = getArguments().getParcelable(ARG_AD_SPOT);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdTypeGroup = (RadioGroup) mRootView.findViewById(R.id.adtype_radiogroup);
        mSdkGroup = (RadioGroup) mRootView.findViewById(R.id.sdk_radiogroup);
        mSdkGroup.setOnCheckedChangeListener(mOnCheckedChangedListener);
        initFragments();
        if (mCurrentAdSpot != null && mViewMode == ViewMode.EDIT) {
            fillFields();
        }
    }

    private void initFragments() {
        if (!areChildFragmentsAdded()) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            addChildFragment(R.id.actionbar_root, ActionBarFragment.newInstance(mViewMode, mCurrentAdSpot), fragmentTransaction);
            addChildFragment(R.id.appkey_editor_root, AppkeyEditorFragment.newInstance(AppkeyEditorFragment.createArguments(mCurrentAdSpot)), fragmentTransaction);
            makeTransaction(fragmentTransaction);
        }
    }

    private void fillFields() {
        EditText editName = (EditText) mRootView.findViewById(R.id.name_value);
        editName.setText(mCurrentAdSpot.getName());

        AdSdk sdk = mCurrentAdSpot.getSdk();
        AdType type = mCurrentAdSpot.getType();

        setAppKeys(sdk);
        setAdType(type);
        setSdkType(sdk);
    }

    private void setSdkType(AdSdk sdk) {
        if (sdk == AdSdk.MOPUB) {
            mSdkGroup.check(R.id.mopub_sdk);
        } else if (sdk == AdSdk.LOOPME) {
            mSdkGroup.check(R.id.loopme_sdk);
        }
    }

    private void setAdType(AdType type) {
        if (type == AdType.BANNER) {
            mAdTypeGroup.check(R.id.banner);
        } else if (type == AdType.INTERSTITIAL) {
            mAdTypeGroup.check(R.id.interstitial);
        }
    }

    private void setAppKeys(AdSdk sdk) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.appkey_editor_root);
        if (fragment != null && fragment instanceof AppkeyEditorFragment) {
            ((AppkeyEditorFragment) fragment).setAppKeys(sdk, mCurrentAdSpot);
        }
    }

    private void getAppropriateEditor(int checkedId) {
        AdSdk sdk = AdSdk.LOOPME;
        if (checkedId == R.id.mopub_sdk) {
            sdk = AdSdk.MOPUB;
        } else if (checkedId == R.id.loopme_sdk) {
            sdk = AdSdk.LOOPME;
        }
        setAppKeyEditor(sdk);
    }

    private void setAppKeyEditor(AdSdk sdk) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.appkey_editor_root);
        if (fragment != null && fragment instanceof AppkeyEditorFragment) {
            ((AppkeyEditorFragment) fragment).setAppkeyEditor(sdk);
        }
    }

    public void onSave() {
        if (isDataValid()) {
            AdSpot adSpot = createNewItem();
            if (adSpot != null) {
                mAdSpotToUpdate = adSpot;
                onCheckAdSpot(adSpot);
            }
        }
    }

    private boolean isDataValid() {
        if (TextUtils.isEmpty(getAdSpotName())) {
            Toast.makeText(mContext, mContext.getString(R.string.empty_ad_spot_name), Toast.LENGTH_LONG).show();
            return false;
        }

        AdSdk sdk = getSelectedSdk();
        String appkey = getAppKey(sdk);

        if (!isValidAppkey(appkey)) {
            Toast.makeText(mContext, getString(R.string.appkey_is_to_short), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void onEditAdSpot(AdSpot adSpot) {
        if (mOnEditAdFragmentListener != null) {
            mOnEditAdFragmentListener.onEdit(adSpot);
        }
    }

    private void onCreateAdSpot(AdSpot adSpot) {
        if (mOnEditAdFragmentListener != null) {
            mOnEditAdFragmentListener.onCreate(adSpot);
        }
    }

    private void onCheckAdSpot(AdSpot adSpot) {
        if (mOnEditAdFragmentListener != null) {
            mOnEditAdFragmentListener.onCheckAdSpot(adSpot);
        }
    }

    private void onCancel() {
        if (mOnEditAdFragmentListener != null) {
            mOnEditAdFragmentListener.onClose();
        }
    }

    private AdSpot createNewItem() {
        AdSpot newAdSpot = new AdSpot();
        AdSdk sdk = getSelectedSdk();
        String adSpotName = getAdSpotName();

        if (mCurrentAdSpot != null) {
            newAdSpot.setAdSpotId(mCurrentAdSpot.getAdSpotId());
        }
        newAdSpot.setSdk(sdk);
        newAdSpot.setType(getSelectedAdType());
        newAdSpot.setName(adSpotName);
        String baseUrl = getBaseUrl(sdk);
        newAdSpot.setBaseUrl(baseUrl);
        String appkey = getAppKey(sdk);

        newAdSpot.setAppKey(appkey);
        newAdSpot.setTime(System.currentTimeMillis());
        return newAdSpot;
    }

    private String getAdSpotName() {
        EditText adSpotNameEditText = (EditText) mRootView.findViewById(R.id.name_value);
        return adSpotNameEditText.getText().toString();
    }

    private boolean isValidAppkey(String appkey) {
        return TextUtils.equals(appkey, SHORT_APP_KEY) || (!TextUtils.isEmpty(appkey) && appkey.length() > 9);
    }

    private String getBaseUrl(AdSdk sdk) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.appkey_editor_root);
        if (fragment != null && fragment instanceof AppkeyEditorFragment) {
            return ((AppkeyEditorFragment) fragment).getBaseUrl(sdk);
        }
        return getString(R.string.empty_string);
    }

    private String getAppKey(AdSdk sdk) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.appkey_editor_root);
        if (fragment != null && fragment instanceof AppkeyEditorFragment) {
            return ((AppkeyEditorFragment) fragment).getAppKey(sdk);
        }
        return getString(R.string.empty_string);
    }

    private AdSdk getSelectedSdk() {
        int sdkId = mSdkGroup.getCheckedRadioButtonId();
        switch (sdkId) {
            case R.id.loopme_sdk:
                return AdSdk.LOOPME;
            case R.id.mopub_sdk:
                return AdSdk.MOPUB;
            default:
                return AdSdk.LOOPME;
        }
    }

    private AdType getSelectedAdType() {
        int typeId = mAdTypeGroup.getCheckedRadioButtonId();
        if (typeId == R.id.banner) {
            return AdType.BANNER;
        } else if (typeId == R.id.interstitial) {
            return AdType.INTERSTITIAL;
        } else {
            return null;
        }
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            getAppropriateEditor(checkedId);
        }
    };

    public static Bundle createArguments(ViewMode viewMode, AdSpot adSpot) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_VIEW_MODE, viewMode);
        args.putParcelable(ARG_AD_SPOT, adSpot);
        return args;
    }

    @Override
    public Bundle getStateBundle() {
        Bundle savedState = new Bundle();
        savedState.putParcelable(ARG_AD_SPOT, mCurrentAdSpot);
        savedState.putSerializable(ARG_VIEW_MODE, mViewMode);
        return savedState;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mOnEditAdFragmentListener = (OnEditAdFragmentListener) context;
    }

    @Override
    public void onDetach() {
        mOnEditAdFragmentListener = null;
        super.onDetach();
    }

    public void onCheckAdSpotResult(boolean isExist, long existedAdSpotId) {
        if (isCreateMode()) {
            handleCreateMode(isExist);
        } else if (isEditMode()) {
            handleEditMode(isExist, existedAdSpotId);
        }
    }

    private void handleEditMode(boolean isExist, long existedAdSpotId) {
        if (isExist && isTheSame(existedAdSpotId)) {
            onEditAdSpot(mAdSpotToUpdate);
        } else {
            Toast.makeText(mContext, R.string.adspot_already_exists, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isTheSame(long existedAdSpotId) {
        return mCurrentAdSpot != null && mCurrentAdSpot.getAdSpotId() == existedAdSpotId;
    }

    private void handleCreateMode(boolean isExist) {
        if (isExist) {
            Toast.makeText(mContext, R.string.adspot_already_exists, Toast.LENGTH_SHORT).show();
        } else {
            onCreateAdSpot(mAdSpotToUpdate);
        }
    }

    private boolean isEditMode() {
        return mViewMode == ViewMode.EDIT;
    }

    private boolean isCreateMode() {
        return mViewMode == ViewMode.CREATE;
    }

    public interface OnEditAdFragmentListener {
        void onCreate(AdSpot adSpot);

        void onEdit(AdSpot adSpot);

        void onClose();

        void onCheckAdSpot(AdSpot adSpot);
    }

    @Override
    public boolean processBackPress() {
        return true;
    }
}
