package com.loopme.tester.qr.view;

import android.support.annotation.NonNull;

import com.loopme.tester.qr.view.fragment.QReaderFragment;
import com.loopme.tester.qr.model.AdDescriptor;
import com.loopme.tester.tracker.AppEventTracker;

public interface QrAdContract {
    interface View {
        void setPresenter(Presenter presenter);

        void addBannerFragment(AdDescriptor adDescriptor);

        void addQReaderFragment(AdDescriptor mAdDescriptor, boolean showReplayView);

        void showMessage(String message);

        void showProgress(boolean show);

        void enableControlsView();

        void resumeQReader();

        void pauseQReader();

        boolean isBannerFragmentOnTop();
    }

    interface Presenter extends QReaderFragment.QReaderListener {
        void onViewCreated();

        void destroy();

        void onBackPressed();

        void track(@NonNull AppEventTracker.Event event, Object... args);
    }
}
