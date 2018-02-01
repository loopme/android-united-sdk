package com.loopme.tester.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.loopme.tester.R;
import com.loopme.tester.enums.AdSdk;
import com.loopme.tester.model.AdSpot;

import static com.loopme.tester.model.AutocompleteKeys.getLoopmeAppKeys;
import static com.loopme.tester.model.AutocompleteKeys.getLoopmeBaseUrls;
import static com.loopme.tester.model.AutocompleteKeys.getMopubAppKeys;

/**
 * Created by katerina on 2/17/17.
 */

public class AppkeyEditorFragment extends BaseFragment implements View.OnClickListener {

    private static final String ARG_AD_SPOT = "ARG_AD_SPOT";

    private ArrayAdapter<String> mAppKeyAdapter;
    private ArrayAdapter<String> mBaseUrlAdapter;
    private AutoCompleteTextView mAppKeyEditText;
    private TextView mBaseUrlEditText;
    private AutoCompleteTextView mAppIdTextView;

    private View mAppkeyLayout;
    private View mAppIdLayout;
    private View mBaseUrlLayout;

    private AdSpot mCurrentAdSpot;

    public static AppkeyEditorFragment newInstance(Bundle args) {
        AppkeyEditorFragment fragment = new AppkeyEditorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loopme_sdk, container, false);
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                mCurrentAdSpot = bundle.getParcelable(ARG_AD_SPOT);
            }
        }
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        if (mCurrentAdSpot != null) {
            setAppKey(mCurrentAdSpot.getAppKey());
            setBaseUrl(mCurrentAdSpot.getBaseUrl());
            setAppkeyEditor(mCurrentAdSpot.getSdk());
            setAppKeys(mCurrentAdSpot.getSdk(), mCurrentAdSpot);
        }
    }

    private void initViews(View view) {
        mAppKeyEditText = (AutoCompleteTextView) view.findViewById(R.id.fragment_adspot_card_appkey_text);
        mBaseUrlEditText = (TextView) view.findViewById(R.id.baseurl);
        mAppIdTextView = (AutoCompleteTextView) view.findViewById(R.id.appid);

        mBaseUrlEditText.setText(com.loopme.Constants.OPEN_RTB_URL);

        mAppkeyLayout = view.findViewById(R.id.appkey_layout);
        mAppIdLayout = view.findViewById(R.id.appid_layout);
        mBaseUrlLayout = view.findViewById(R.id.baseurl_layout);

        mAppIdTextView.setOnClickListener(this);
        mAppKeyEditText.setOnClickListener(this);
        mBaseUrlEditText.setOnClickListener(this);

        mAppIdTextView.setThreshold(1);
        mAppKeyEditText.setThreshold(1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_adspot_card_appkey_text: {
                showAppKeyDropDown();
                break;
            }
            case R.id.baseurl: {
                break;
            }
        }
    }

    public void setAppkeyEditor(AdSdk sdk) {
        switch (sdk) {
            case LOOPME: {
                setLoopmeView();
                break;
            }
            case LMVPAID: {
                setVpaidView();
                break;
            }
            case MOPUB: {
                setMopubView();
                break;
            }
        }
    }

    private void setMopubView() {
        mAppkeyLayout.setVisibility(View.GONE);
        mBaseUrlLayout.setVisibility(View.GONE);
        mAppIdLayout.setVisibility(View.VISIBLE);
        mAppKeyAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getMopubAppKeys());
    }

    private void setVpaidView() {
        mAppkeyLayout.setVisibility(View.VISIBLE);
        mBaseUrlLayout.setVisibility(View.GONE);
        mAppIdLayout.setVisibility(View.GONE);
        mAppKeyAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getLoopmeAppKeys());
    }

    private void setLoopmeView() {
        mAppkeyLayout.setVisibility(View.VISIBLE);
        mBaseUrlLayout.setVisibility(View.VISIBLE);
        mAppIdLayout.setVisibility(View.GONE);
        mAppKeyAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getLoopmeAppKeys());
        mBaseUrlAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, getLoopmeBaseUrls());

    }

    private void showAppKeyDropDown() {
        mAppKeyEditText.setAdapter(mAppKeyAdapter);
        mAppKeyEditText.showDropDown();
    }

    public String getAppKey(AdSdk sdk) {
        switch (sdk) {
            case LOOPME: {
                return getAppKey();
            }
            case MOPUB: {
                return getUnitId();
            }
            case LMVPAID: {
                return getAppKey();
            }
            default:
                return getString(R.string.empty_string);
        }
    }

    public String getBaseUrl(AdSdk sdk) {
        switch (sdk) {
            case LOOPME: {
                return getBaseUrl();
            }
            default: {
                return getBaseUrl();
            }
        }
    }

    private String getUnitId() {
        return mAppIdTextView != null ? mAppIdTextView.getText().toString() : getString(R.string.empty_string);
    }

    public String getBaseUrl() {
        return mBaseUrlEditText != null ? mBaseUrlEditText.getText().toString() : getString(R.string.empty_string);
    }

    public String getAppKey() {
        return mAppKeyEditText != null ? mAppKeyEditText.getText().toString() : getString(R.string.empty_string);
    }

    public void setAppKeys(AdSdk sdk, AdSpot adSpot) {
        if (sdk == AdSdk.LOOPME) {
            setLoopmeKeys(adSpot);
        } else if (sdk == AdSdk.LMVPAID) {
            setVpaidKeys(adSpot);
        } else if (sdk == AdSdk.MOPUB) {
            setMopubKeys(adSpot);
        } else {
            setLoopmeKeys(adSpot);
        }
    }

    private void setMopubKeys(AdSpot adSpot) {
        setAdUnitId(adSpot.getAppKey());
    }

    private void setVpaidKeys(AdSpot adSpot) {
        setAppKey(adSpot.getAppKey());
    }

    private void setLoopmeKeys(AdSpot adSpot) {
        setBaseUrl(adSpot.getBaseUrl());
        setAppKey(adSpot.getAppKey());
    }

    public void setBaseUrl(String baseUrl) {
        if (mBaseUrlEditText != null) {
            mBaseUrlEditText.setText(baseUrl);
        }
    }

    public void setAppKey(String appKey) {
        if (mAppKeyEditText != null) {
            mAppKeyEditText.setText(appKey);
        }
    }

    public void setAdUnitId(String appKey) {
        if (mAppIdTextView != null) {
            mAppIdTextView.setText(appKey);
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

}
