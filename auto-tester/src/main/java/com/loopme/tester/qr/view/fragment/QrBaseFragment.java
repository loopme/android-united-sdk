package com.loopme.tester.qr.view.fragment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.transition.Slide;
import android.support.v4.app.Fragment;
import android.view.Gravity;

import com.loopme.tester.qr.model.AdDescriptor;

public class QrBaseFragment extends Fragment {
    protected static final String ARG_AD_DESCRIPTOR = "ARG_AD_DESCRIPTOR";
    protected AdDescriptor mAdDescriptor;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            mAdDescriptor = getArguments().getParcelable(ARG_AD_DESCRIPTOR);
        }
    }

    @Nullable
    @Override
    public Object getEnterTransition() {
        return new Slide(Gravity.END);
    }

    @Nullable
    @Override
    public Object getExitTransition() {
        return new Slide(Gravity.START);
    }

    @Override
    public boolean getAllowEnterTransitionOverlap() {
        return true;
    }

    @Override
    public boolean getAllowReturnTransitionOverlap() {
        return true;
    }
}
