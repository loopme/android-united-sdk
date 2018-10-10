package com.loopme.tester.qr;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.loopme.tester.R;
import com.loopme.tester.qr.view.QrAdPresenter;
import com.loopme.tester.qr.view.fragment.QrAdFragment;
import com.loopme.tester.utils.UiUtils;

public class QRAdActivity extends AppCompatActivity {
    private QrAdPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_ad);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        QrAdFragment qrAdFragment = QrAdFragment.newInstance();
        mPresenter = new QrAdPresenter(this, qrAdFragment);
        UiUtils.addFragment(getSupportFragmentManager(), R.id.activity_qr_ad_root, qrAdFragment);
        UiUtils.makeActivitySlidable(this);
    }

    @Override
    public void onBackPressed() {
        if (mPresenter != null) {
            mPresenter.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        super.onDestroy();
    }

}
