package com.loopme;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.loopme.utils.Utils;

import java.io.IOException;

public class LoopMeMediaPlayer {
    private static final String LOG_TAG = LoopMeMediaPlayer.class.getSimpleName();
    private final MediaPlayer mMediaPlayer;
    private final LoopMeMediaPlayerListener mListener;
    private final Handler handler = new Handler();
    private final Runnable onTimeChange = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayer.isPlaying()) {
                mListener.onTimeUpdate(mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
                handler.postDelayed(this, 1000);
            }
        }
    };

    public LoopMeMediaPlayer(@NonNull String source, @NonNull LoopMeMediaPlayerListener listener) {
        mMediaPlayer = new MediaPlayer();
        mListener = listener;
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
            .build();
        mMediaPlayer.setLooping(false);
        mMediaPlayer.setAudioAttributes(audioAttributes);
        mMediaPlayer.setOnPreparedListener(mListener);
        mMediaPlayer.setOnErrorListener(mListener);
        mMediaPlayer.setOnCompletionListener(mListener);
        try {
            mMediaPlayer.setDataSource(source);
            mMediaPlayer.prepareAsync();
        } catch (IllegalStateException | IOException e) {
            mListener.onErrorOccurred(e);
        }
    }

    public void muteVideo(boolean muted) {
        float volume = muted ? 0f : Utils.getSystemVolume();
        mMediaPlayer.setVolume(volume, volume);
        mListener.onVolumeChanged(volume, mMediaPlayer.getCurrentPosition());
    }

    public void releasePlayer() {
        handler.removeCallbacks(onTimeChange);
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }

    public void setSurface(Surface surface) {
        try {
            mMediaPlayer.setSurface(surface);
        } catch (IllegalStateException e) {
            mListener.onErrorOccurred(e);
        }
    }

    public boolean isPlaying() {
        try {
            return mMediaPlayer.isPlaying();
        } catch (IllegalStateException e) {
            mListener.onErrorOccurred(e);
        }
        return false;
    }

    public void destroyListeners() {
        mMediaPlayer.setOnErrorListener(null);
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.setOnCompletionListener(null);
    }

    public void pauseMediaPlayer() {
        try {
            if (isPlaying()) {
                handler.removeCallbacks(onTimeChange);
                mMediaPlayer.pause();
            }
        } catch (IllegalStateException e) {
            mListener.onErrorOccurred(e);
        }
    }

    public void start() {
        try {
            if (!isPlaying()) {
                mMediaPlayer.start();
                handler.removeCallbacks(onTimeChange);
                handler.postDelayed(onTimeChange, 1000);
            }
        } catch (IllegalStateException e) {
            mListener.onErrorOccurred(e);
        }
    }

    public int getVideoDuration() { return mMediaPlayer.getDuration(); }

    public interface LoopMeMediaPlayerListener extends
            MediaPlayer.OnPreparedListener,
            MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener {

        void onTimeUpdate(int currentTime, int duration);
        void onErrorOccurred(Exception e);
        void onVolumeChanged(float volume, int currentPosition);
    }
}
