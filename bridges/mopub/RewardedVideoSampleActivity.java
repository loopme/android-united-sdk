package com.loopme.bridge.mopub;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.mopub.common.MoPubReward;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideoManager;
import com.mopub.mobileads.MoPubRewardedVideos;

import java.util.Set;

public class RewardedVideoSampleActivity extends Activity implements
        View.OnClickListener,
        MoPubRewardedVideoListener {

    private static final String AD_UNIT_ID_REWARDED_VIDEO = "57977d46e8304f4c8cf2ab44eb1e0ab7";//Your mopub key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MoPubRewardedVideos.initializeRewardedVideo(this);
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
        if (MoPubRewardedVideos.hasRewardedVideo(AD_UNIT_ID_REWARDED_VIDEO)) {
            MoPubRewardedVideos.showRewardedVideo(AD_UNIT_ID_REWARDED_VIDEO);
        }
    }

    private void loadRewardedVideo() {
        MoPubRewardedVideoManager.updateActivity(this);
        MoPubRewardedVideos.loadRewardedVideo(AD_UNIT_ID_REWARDED_VIDEO);
    }

    @Override
    public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
        Toast.makeText(RewardedVideoSampleActivity.this, "Rewarded video is ready", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
        Toast.makeText(RewardedVideoSampleActivity.this, "Fail: " + errorCode.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted(@NonNull String adUnitId) {
        Toast.makeText(RewardedVideoSampleActivity.this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
        Toast.makeText(RewardedVideoSampleActivity.this, "onRewardedVideoPlaybackError", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoClosed(@NonNull String adUnitId) {
        Toast.makeText(RewardedVideoSampleActivity.this, "onRewardedVideoClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
        String rewardMessage = "your reward: " + reward.getAmount() + " " + reward.getLabel();
        Toast.makeText(RewardedVideoSampleActivity.this, rewardMessage, Toast.LENGTH_SHORT).show();
    }
}