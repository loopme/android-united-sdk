package com.loopme.tester.ui.fragment.screen;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopme.tester.Constants;
import com.loopme.tester.R;
import com.loopme.tester.ui.activity.BaseActivity;
import com.loopme.tester.ui.fragment.BaseFragment;
import com.loopme.utils.FileUtils;
import com.mopub.common.MoPub;

/**
 * Created by katerina on 2/15/17.
 */

public class InfoFragment extends BaseFragment implements View.OnClickListener {


    private OnInfoFragmentListener mOnInfoFragmentListener;
    private Context mContext;

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setVersioningNames(view);
        configureAutoLoadingState(view);
        view.findViewById(R.id.fragment_info_container).setOnClickListener(this);
        view.findViewById(R.id.fragment_info_check_update_textview).setOnClickListener(this);
        view.findViewById(R.id.fragment_info_clear_cache_button).setOnClickListener(this);
        view.findViewById(R.id.fragment_info_import_button).setOnClickListener(this);
        view.findViewById(R.id.fragment_info_export_button).setOnClickListener(this);
    }

    private void configureAutoLoadingState(View view) {
        Switch enableAutoLoading = (Switch) view.findViewById(R.id.enable_auoloading_checkbox);
        enableAutoLoading.setChecked(getAutoLoadingState());
        enableAutoLoading.setOnCheckedChangeListener(mOnCheckeChangeListener);
    }

    private boolean getAutoLoadingState() {
        return mContext instanceof BaseActivity && ((BaseActivity) mContext).getAutoLoadingState();
    }

    private CompoundButton.OnCheckedChangeListener mOnCheckeChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean state) {
            setAutoLoadingState(state);
        }
    };

    private void setAutoLoadingState(boolean autoLoadingState) {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).setAutoLoadingState(autoLoadingState);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void setVersioningNames(View view) {
        String versionName = com.loopme.tester.BuildConfig.VERSION_NAME;
        int versionCode = com.loopme.tester.BuildConfig.VERSION_CODE;

        String testerVersion = getString(R.string.fragment_info_tester_version, versionName, versionCode);
        TextView testerVersionTextView = (TextView) view.findViewById(R.id.fragment_info_tester_version);
        testerVersionTextView.setText(testerVersion);

        String loopmeVersion = getString(R.string.fragment_info_loopme_sdk_version, com.loopme.BuildConfig.VERSION_NAME);
        TextView loopmeVersionTextView = (TextView) view.findViewById(R.id.fragment_info_loopme_sdk_version);
        loopmeVersionTextView.setText(loopmeVersion);

        String mopubVersion = getString(R.string.fragment_info_mopub_sdk_version, MoPub.SDK_VERSION);
        TextView mopubVersionTextView = (TextView) view.findViewById(R.id.fragment_info_mopub_sdk_version);
        mopubVersionTextView.setText(mopubVersion);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mOnInfoFragmentListener = (OnInfoFragmentListener) context;
    }

    @Override
    public void onDestroy() {
        mOnInfoFragmentListener = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_info_container: {
                dismissInfoFragment();
                break;
            }
            case R.id.fragment_info_clear_cache_button: {
                clearCache();
                dismissInfoFragment();
                break;
            }
            case R.id.fragment_info_import_button: {
                importKeys();
                break;
            }
            case R.id.fragment_info_export_button: {
                exportKeys();
                break;
            }
            case R.id.fragment_info_check_update_textview: {
                checkUpdates();
                break;
            }
        }
    }

    private void checkUpdates() {
        String url = Constants.TESTFAIRY_URL;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void exportKeys() {
        showExportFragment();
    }

    private void importKeys() {
        showImportFragment();
    }

    private void clearCache() {
        FileUtils.clearCache(mContext);
        Toast.makeText(mContext, getString(R.string.cache_removed), Toast.LENGTH_LONG).show();
    }

    private void showImportFragment() {
        if (mOnInfoFragmentListener != null) {
            mOnInfoFragmentListener.showImportFragment();
        }
    }

    private void showExportFragment() {
        if (mOnInfoFragmentListener != null) {
            mOnInfoFragmentListener.showExportFragment();
        }
    }

    @Deprecated
    private void showLogFragment() {
        if (mOnInfoFragmentListener != null) {
            mOnInfoFragmentListener.showLogFragment();
        }
    }

    private void dismissInfoFragment() {
        if (mOnInfoFragmentListener != null) {
            mOnInfoFragmentListener.dismissInfoFragment();
        }
    }

    public interface OnInfoFragmentListener {

        void showImportFragment();

        void showExportFragment();

        void showLogFragment();

        void dismissInfoFragment();
    }

    @Override
    public boolean processBackPress() {
        return true;
    }
}
