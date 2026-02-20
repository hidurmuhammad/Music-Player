package com.codmeric.musicplayer

import android.app.Application
import android.util.Log
import com.codmeric.musicplayer.data.AudioRepository
import com.codmeric.musicplayer.player.EqualizerManager
import com.codmeric.musicplayer.player.MediaManager

class MusicPlayerApp : Application() {

    lateinit var repository: AudioRepository
    lateinit var mediaManager: MediaManager
    lateinit var equalizerManager: EqualizerManager

    override fun onCreate() {
        super.onCreate()
        
        try {
            //Application dependencies initialized
            repository = AudioRepository(this)
            mediaManager = MediaManager(this)
            equalizerManager = EqualizerManager()
        } catch (e: Exception) {
            Log.e("MusicPlayer", "Error initializing Application dependencies", e)
        }
    }
}
