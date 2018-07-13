package com.loopme.tester.ui.fragment.screen;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.loopme.tester.AppUpdateChecker;
import com.loopme.tester.R;
import com.loopme.tester.ui.activity.BaseActivity;
import com.loopme.tester.ui.fragment.BaseFragment;
import com.loopme.tester.utils.Utils;
import com.loopme.utils.FileUtils;
import com.mopub.common.MoPub;

/**
 * Created by katerina on 2/15/17.
 */

public class InfoFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    private OnInfoFragmentListener mOnInfoFragmentListener;
    private Context mContext;

    public static InfoFragment newInstance() {
        return new InfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setRootView(container);
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
        initGdprIntegrationSpinner(view);
    }

    private void configureAutoLoadingState(View view) {
        Switch enableAutoLoading = view.findViewById(R.id.enable_auoloading_checkbox);
        enableAutoLoading.setChecked(getAutoLoadingState());
        enableAutoLoading.setOnCheckedChangeListener(mOnCheckeChangeListener);
    }

    private void initGdprIntegrationSpinner(View view) {
        Spinner spinner = view.findViewById(R.id.integration_type_spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext, R.array.integration_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        GdprIntegrationCase gdprCase = GdprIntegrationCase.IGNORE;
        if (mContext instanceof BaseActivity) {
            gdprCase = ((BaseActivity) mContext).getGdprIntegrationCase();
        }
        spinner.setSelection(gdprCase.ordinal());
        spinner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = ((Spinner) v).getSelectedItemPosition();
                String description = GdprIntegrationCase.getDescription(position);
                Toast.makeText(mContext, description, Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        GdprIntegrationCase gdprCase = GdprIntegrationCase.valueOf((String) parent.getItemAtPosition(position));
        if (mContext instanceof BaseActivity)
            ((BaseActivity) mContext).setGdprIntegrationCase(gdprCase);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

        String loopMeFullVersion = com.loopme.BuildConfig.VERSION_CODE + "." + com.loopme.BuildConfig.VERSION_NAME;
        String loopmeVersion = getString(R.string.fragment_info_loopme_sdk_version, loopMeFullVersion);
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
                new AppUpdateChecker(getActivity(), AppUpdateChecker.LaunchMode.INFO).checkUpdate();
                break;
            }
        }
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

    public enum GdprIntegrationCase {
        IGNORE("Means Publisher ignore LoopMe SDK integration advices, and did nothing"),
        INIT("Means Publisher call LoopMeSdk.init(activity) method. Sdk should ask user about consent"),
        CONSENT_TRUE("Publisher explicitly passes user consent value  as true to sdk"),
        CONSENT_FALSE("Publisher explicitly passes user consent  value as false to sdk");

        private String mDesc;

        GdprIntegrationCase(String desc) {
            mDesc = desc;
        }

        public String getDesc() {
            return mDesc;
        }

        public static String getDescription(int position) {
            return values()[position].getDesc();
        }
    }
}
