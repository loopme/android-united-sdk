package com.loopme;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;

import com.loopme.utils.Utils;

import java.io.IOException;

public class LoopMeMediaPlayer {
    private static final String LOG_TAG = LoopMeMediaPlayer.class.getSimpleName();
    private static final int START_POSITION = 0;
    private static final int START_POSITION_10 = 10;
    private static final float DEFAULT_RIGHT_VOLUME = 0f;
    private static final float DEFAULT_LEFT_VOLUME = 0f;
    private MediaPlayer mMediaPlayer;
    private LoopMeMediaPlayerListener mListener;

    public LoopMeMediaPlayer(String source, LoopMeMediaPlayerListener listener) {
        mMediaPlayer = new MediaPlayer();
        setDataSource(source);
        configurePlayer();
        setListener(listener);
        prepareAsync();
    }

    private void configurePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(false);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    private void setListener(LoopMeMediaPlayerListener listener) {
        if (mMediaPlayer != null && listener != null) {
            mListener = listener;
            mMediaPlayer.setOnPreparedListener(listener);
            mMediaPlayer.setOnErrorListener(listener);
            mMediaPlayer.setOnCompletionListener(listener);
        }
    }

    public void muteVideo(boolean muted) {
        if (muted) {
            setMuteVolume();
        } else {
            setSystemVolume();
        }
    }

    private void setMuteVolume() {
        setVolume(DEFAULT_LEFT_VOLUME, DEFAULT_RIGHT_VOLUME);
        onVolumeChanged(DEFAULT_LEFT_VOLUME, getCurrentPosition());
    }

    private void setVolume(float leftVolume, float rightVolume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    public void setSystemVolume() {
        float systemVolume = Utils.getSystemVolume();
        setVolume(systemVolume, systemVolume);
        onVolumeChanged(systemVolume, getCurrentPosition());
    }

    public int getCurrentPosition() {
        return mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
    }

    public void releasePlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
    }

    public void setSurface(Surface surface) {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.setSurface(surface);
            }
        } catch (IllegalStateException e) {
            onErrorOccurred(e);
        }
    }

    private void setDataSource(String filePath) {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.setDataSource(filePath);
            }
        } catch (IllegalStateException | IOException e) {
            onErrorOccurred(e);
        }
    }


    private void prepareAsync() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.prepareAsync();
            }
        } catch (IllegalStateException e) {
            onErrorOccurred(e);
        }
    }

    public boolean isPlaying() {
        try {
            return mMediaPlayer != null && mMediaPlayer.isPlaying();
        } catch (IllegalStateException e) {
            onErrorOccurred(e);
        }
        return false;
    }

    public void destroyListeners() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.setOnPreparedListener(null);
            mMediaPlayer.setOnCompletionListener(null);
        }
    }

    public void pauseMediaPlayer() {
        try {
            if (isPlaying()) {
                mMediaPlayer.pause();
            }
        } catch (IllegalStateException e) {
            onErrorOccurred(e);
        }
    }

    public void start() {
        try {
            if (!isPlaying()) {
                mMediaPlayer.start();
            }
        } catch (IllegalStateException e) {
            onErrorOccurred(e);
        }
    }

    public void seekTo(int position) {
        try {
            if (mMediaPlayer != null) {
                if (position == START_POSITION_10) {
                    mMediaPlayer.seekTo(START_POSITION);
                } else {
                    mMediaPlayer.seekTo(position);
                }
            }
        } catch (IllegalStateException e) {
            onErrorOccurred(e);
        }
    }

    public int getVideoDuration() {
        return mMediaPlayer == null ? 0 : mMediaPlayer.getDuration();
    }

    private void onErrorOccurred(Exception e) {
        e.printStackTrace();
        Logging.out(LOG_TAG, e.getMessage());
        if (mListener != null) {
            mListener.onErrorOccurred(e);
        }
    }

    private void onVolumeChanged(double volume, int currentPosition) {
        if (mListener != null) {
            mListener.onVolumeChanged(volume, currentPosition);
        }
    }

    public interface LoopMeMediaPlayerListener extends
            MediaPlayer.OnPreparedListener,
            MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener {

        void onErrorOccurred(Exception e);
        void onVolumeChanged(double volume, int currentPosition);
    }
}
