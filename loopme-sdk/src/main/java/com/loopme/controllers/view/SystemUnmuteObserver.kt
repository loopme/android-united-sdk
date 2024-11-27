package com.loopme.controllers.view

import android.content.Context
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler

class SystemUnmuteObserver(private val onUnmuteListener: () -> Void, context: Context, handler: Handler) : ContentObserver(handler) {
    private val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var previousVolume: Int = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        val currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
        val delta = currentVolume - previousVolume
        if (previousVolume == 0 && delta > 0) onUnmuteListener.invoke()
        previousVolume = currentVolume
    }

}