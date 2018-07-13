package com.loopme.tester.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.loopme.tester.Constants;
import com.loopme.tester.R;
import com.loopme.tester.ui.qr.LoopMeQReader;
import com.loopme.tester.utils.UiUtils;

import github.nisrulz.qreader.QRDataListener;

public class QReaderActivity extends AppCompatActivity implements QRDataListener {
    private LoopMeQReader mLoopMeQReader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_qr_layout);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.camera_qr_view);
        mLoopMeQReader = new LoopMeQReader(surfaceView, this);
        UiUtils.makeActivitySlidable(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoopMeQReader.resume();
    }

    @Override
    protected void onPause() {
        mLoopMeQReader.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLoopMeQReader.destroy();
        super.onDestroy();
    }

    @Override
    public void onDetected(final String url) {
        Intent intent = new Intent();
        intent.putExtra(Constants.ARG_QR_AD_URL_FROM_QR, url);
        setResult(AppCompatActivity.RESULT_OK, intent);
        finish();
    }
}
