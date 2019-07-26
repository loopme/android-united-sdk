package com.loopme.mopub_mediation_sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideoManager;
import com.mopub.mobileads.MoPubRewardedVideos;

import java.util.Set;

public class RewardedVideoActivity extends Activity implements
        View.OnClickListener,
        MoPubRewardedVideoListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewarded);

        MoPubRewardedVideos.setRewardedVideoListener(this);

        findViewById(R.id.load_rewarded_video_button).setOnClickListener(this);
        findViewById(R.id.show_rewarded_video_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load_rewarded_video_button: {
                loadRewardedVideo();
                break;
            }
            case R.id.show_rewarded_video_button: {
                showRewardedVideo();
                break;
            }
            default:
                break;
        }
    }

    private void showRewardedVideo() {
        if (MoPubRewardedVideos.hasRewardedVideo(BuildConfig.AD_UNIT_ID_REWARDED)) {
            MoPubRewardedVideos.showRewardedVideo(BuildConfig.AD_UNIT_ID_REWARDED);
        }
    }

    private void loadRewardedVideo() {
        MoPubRewardedVideoManager.updateActivity(this);
        MoPubRewardedVideos.loadRewardedVideo(BuildConfig.AD_UNIT_ID_REWARDED);
    }

    @Override
    public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
        Toast.makeText(this, "Rewarded video is ready", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
        Toast.makeText(this, "Fail: " + errorCode.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted(@NonNull String adUnitId) {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
        Toast.makeText(this, "onRewardedVideoPlaybackError", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoClicked(@NonNull String adUnitId) {

    }

    @Override
    public void onRewardedVideoClosed(@NonNull String adUnitId) {
        Toast.makeText(this, "onRewardedVideoClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
        String rewardMessage = "your reward: " + reward.getAmount() + " " + reward.getLabel();
        Toast.makeText(this, rewardMessage, Toast.LENGTH_SHORT).show();
    }
}