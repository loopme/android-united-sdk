package com.loopme;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.VideoSize;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.database.DatabaseProvider;
import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.cache.Cache;
import androidx.media3.datasource.cache.CacheDataSource;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;
import androidx.media3.datasource.okhttp.OkHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;

import com.loopme.tracker.MediaPlayerTracker;
import com.loopme.utils.Utils;
import com.loopme.utils.VideoSessionManager;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;

@UnstableApi
public class LoopMeMediaPlayer {
    private static final String LOG_TAG = LoopMeMediaPlayer.class.getSimpleName();
    private final ExoPlayer player;
    private final LoopMeMediaPlayerListener mListener;
    private final Handler handler = new Handler();
    private static Cache simpleCache;
    private final Runnable onTimeChange = new Runnable() {
        @Override
        public void run() {
            int playbackState = player.getPlaybackState();
            if (playbackState == Player.STATE_READY || playbackState == Player.STATE_BUFFERING) {
                mListener.onTimeUpdate((int) player.getCurrentPosition(), (int) player.getDuration());
                handler.postDelayed(this, 1000);
            }
        }
    };


    @UnstableApi
    public LoopMeMediaPlayer(
            @NonNull Context context,
            @NonNull String sourceUrl,
            @NonNull VideoSessionManager videoSessionManager,
            @NonNull LoopMeMediaPlayerListener listener) {

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();

        OkHttpDataSource.Factory okHttpDataSourceFactory = new OkHttpDataSource.Factory(okHttpClient);

        DataSource.Factory cacheDataSourceFactory =
                new CacheDataSource.Factory()
                        .setCache(getCache(context))
                        .setUpstreamDataSourceFactory(okHttpDataSourceFactory)
                        .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                        .setEventListener(new CacheDataSource.EventListener() {
                            @Override
                            public void onCachedBytesRead(long cacheSizeBytes, long cachedBytesRead) {
                                Log.d(LOG_TAG, "Cached bytes read: " + cachedBytesRead);
                            }

                            @Override
                            public void onCacheIgnored(int reason) {
                                if (reason == CacheDataSource.CACHE_IGNORED_REASON_ERROR) {
                                    MediaPlayerTracker.trackCacheError(new IOException("Cache ignored error"), sourceUrl);
                                }
                            }
                        });

        mListener = listener;

        player = new ExoPlayer.Builder(context)
                .setMediaSourceFactory(
                        new DefaultMediaSourceFactory(context).setDataSourceFactory(cacheDataSourceFactory))
                .setLoadControl(new DefaultLoadControl())
                .build();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .setUsage(C.USAGE_MEDIA)
                .build();
        player.setAudioAttributes(audioAttributes, true);
        player.setRepeatMode(Player.REPEAT_MODE_OFF);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        if (videoSessionManager.isBuffering()) {
                            videoSessionManager.endBuffering();
                        }
                        mListener.onPrepared();
                        break;
                    case Player.STATE_ENDED:
                        mListener.onCompletion();
                        if (videoSessionManager.isBuffering()) {
                            videoSessionManager.endBuffering();
                        }
                        break;
                    case Player.STATE_BUFFERING:
                        if (!videoSessionManager.isBuffering()) {
                            videoSessionManager.startBuffering();
                        }
                        break;
                    case Player.STATE_IDLE:
                        break;
                }
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Throwable cause = error.getCause();
                if (cause instanceof IOException) {
                    Log.e(LOG_TAG, "Caching error detected: " + cause.getMessage());
                    MediaPlayerTracker.trackCacheError((Exception)cause, sourceUrl);
                } else {
                    Log.e(LOG_TAG, "Player error: " + error.getMessage());
                    mListener.onErrorOccurred(error);
                }
            }

        });

        MediaItem mediaItem = MediaItem.fromUri(sourceUrl);
        player.addMediaItem(mediaItem);
        player.prepare();
    }

    public static synchronized Cache getCache(Context context) {
        if (simpleCache == null) {

            long cacheSizeBytes = 100 * 1024 * 1024;
            File cacheDir = new File(context.getCacheDir(), "video_cache");

            DatabaseProvider databaseProvider = new StandaloneDatabaseProvider(context);
            simpleCache = new SimpleCache(
                    cacheDir,
                    new LeastRecentlyUsedCacheEvictor(cacheSizeBytes),
                    databaseProvider
            );
        }
        return simpleCache;
    }

    public void muteVideo(boolean muted) {
        float volume = muted ? 0f : Utils.getSystemVolume();
        player.setVolume(volume);
        mListener.onVolumeChanged(volume, (int) player.getCurrentPosition());
    }

    public void releasePlayer() {
        handler.removeCallbacks(onTimeChange);
        player.release();
    }

    public void setSurface(Surface surface) {
        player.setVideoSurface(surface);
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void destroyListeners() {
        player.removeListener(this.mListener);
    }

    public void pauseMediaPlayer() {
        if (isPlaying()) {
            handler.removeCallbacks(onTimeChange);
            player.pause();
        }
    }

    public void start() {
        if (!isPlaying()) {
            player.play();
            handler.removeCallbacks(onTimeChange);
            handler.postDelayed(onTimeChange, 1000);
        }
    }

    public int getVideoDuration() {
        return (int) player.getDuration();
    }

    public VideoSize getVideoSize() {
        return player.getVideoSize();
    }

    public interface LoopMeMediaPlayerListener extends Player.Listener {
        void onTimeUpdate(int currentTime, int duration);
        void onErrorOccurred(Exception e);
        void onVolumeChanged(float volume, int currentPosition);
        void onPrepared();
        void onCompletion();
    }
}
