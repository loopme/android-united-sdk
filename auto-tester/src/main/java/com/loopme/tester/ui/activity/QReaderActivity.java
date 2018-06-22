package com.loopme.tester.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.loopme.tester.R;
import com.loopme.tester.ui.qr.LoopMeQReader;
import com.loopme.tester.utils.UiUtils;

import github.nisrulz.qreader.QRDataListener;

public class QReaderActivity extends AppCompatActivity implements QRDataListener {
    private static final int PERMISSION_CAMERA_CODE = 900;
    public static final String ARG_AD_URL = "ARG_AD_URL";
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
        if (checkCameraPermission()) {
            mLoopMeQReader.resume();
        } else {
            askCameraPermission();
        }
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
        intent.putExtra(ARG_AD_URL, url);
        setResult(AppCompatActivity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mLoopMeQReader.resume();
        }
    }

    private void askCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_CODE);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}
