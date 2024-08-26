package com.loopme.loaders;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.loopme.common.LoopMeError;
import com.loopme.models.Errors;
import com.loopme.network.GetResponse;
import com.loopme.vast.VastVpaidEventTracker;
import com.loopme.vast.WrapperParser;
import com.loopme.network.LoopMeAdService;
import com.loopme.xml.vast4.VastInfo;
import com.loopme.xml.vast4.Wrapper;

import java.util.ArrayList;
import java.util.List;

public class VastWrapperFetcher {

    private static final String LOG_TAG = VastWrapperFetcher.class.getSimpleName();
    private static final int VAST_4_MAX_WRAPPERS_COUNT = 5;
    private int mCurrentWrapper = 1;
    private final String mVastString;
    private final Listener mListener;
    private final List<Wrapper> mWrappersList = new ArrayList<>();
    private volatile boolean mIsCanceled;

    public VastWrapperFetcher(@NonNull String vastString, @NonNull Listener listener) {
        mVastString = vastString;
        mListener = listener;
        mIsCanceled = false;
    }

    public void start() { handleVastWrapperCase(mVastString); }

    public void cancel() { mIsCanceled = true; }

    private void handleVastWrapperCase(String vastString) {
        if (mIsCanceled) return;
        VastInfo vastInfo = VastInfo.getVastInfo(vastString);
        if (vastInfo.hasError()) {
            onFailed(Errors.NO_VAST_RESPONSE_AFTER_WRAPPER);
            return;
        }
        Wrapper wrapper = vastInfo.getWrapper();
        if (wrapper == null) {
            mListener.onCompleted(vastString, mWrappersList);
            return;
        }
        mWrappersList.add(wrapper);
        if (++mCurrentWrapper > VAST_4_MAX_WRAPPERS_COUNT) {
            onFailed(Errors.WRAPPER_LIMIT_REACHED);
            return;
        }
        String vastTagUrl = wrapper.getVastTagUrl();
        if (TextUtils.isEmpty(vastTagUrl)) {
            onFailed(Errors.GENERAL_WRAPPER_ERROR);
            return;
        }
        LoopMeAdService.downloadResource(vastTagUrl, new LoopMeAdService.Listener() {
            @Override
            public void onSuccess(GetResponse<String> response) { handleVastWrapperCase(response.getBody()); }
            @Override
            public void onError(Exception e) { onFailed(Errors.NO_VAST_RESPONSE_AFTER_WRAPPER); }
        });
    }

    private void onFailed(LoopMeError error) {
        WrapperParser parser = new WrapperParser(mWrappersList);
        for (String errorUrl : parser.getErrorUrlList()) {
            VastVpaidEventTracker.trackVastEvent(errorUrl, String.valueOf(error.getErrorCode()));
        }
        mListener.onFailed(error);
        cancel();
    }

    public interface Listener {
        void onCompleted(String vastString, List<Wrapper> wrapperList);
        void onFailed(LoopMeError error);
    }
}
