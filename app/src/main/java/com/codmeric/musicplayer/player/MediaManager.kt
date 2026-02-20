package com.codmeric.musicplayer.player

import kotlin.random.Random
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.codmeric.musicplayer.data.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MediaManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition
    
    private val _currentTrack = MutableStateFlow<AudioTrack?>(null)
    val currentTrack: StateFlow<AudioTrack?> = _currentTrack

    fun setCurrentTrack(track: AudioTrack) {
        _currentTrack.value = track
    }

    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    fun playTrack(track: AudioTrack) {
        _currentTrack.value = track
        try {
            mediaPlayer?.release()
            
            mediaPlayer = MediaPlayer().apply {
                val afd = context.assets.openFd(track.assetPath)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                setOnPreparedListener {
                    start()
                    _isPlaying.value = true
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPosition.value = it.duration // Ensure it shows full duration at end
                    stopProgressUpdate()
                }
                setOnErrorListener { _, what, extra ->
                    _isPlaying.value = false
                    stopProgressUpdate()
                    true
                }
                prepareAsync()
            }
            
            startProgressUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
            _isPlaying.value = false
            mediaPlayer = null
        }
    }

    fun togglePlayPause() {
        try {
            mediaPlayer?.let {
                // it.isPlaying can only be called in {Prepared, Started, Paused, PlaybackCompleted} states.
                // In context of this app, we check if it is already prepared by the _isPlaying state or similar.
                // However, a safer check is to catch IllegalStateException if it's not ready.
                if (it.isPlaying) {
                    it.pause()
                    _isPlaying.value = false
                } else {
                    it.start()
                    _isPlaying.value = true
                    startProgressUpdate()
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Log.e("MusicPlayer", "togglePlayPause called in invalid state")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun seekTo(position: Int) {
        try {
            mediaPlayer?.seekTo(position)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Log.e("MusicPlayer", "seekTo called in invalid state")
        }
    }

    fun release() {
        stopProgressUpdate()
        mediaPlayer?.release()
        mediaPlayer = null
        _isPlaying.value = false
    }

    fun isMediaPlayerReady(): Boolean = mediaPlayer != null

    fun getAudioSessionId(): Int = mediaPlayer?.audioSessionId ?: 0

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (true) {
                mediaPlayer?.let {
                    try {
                        if (it.isPlaying) {
                            _currentPosition.value = it.currentPosition
                        }
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                        // Not ready yet
                    }
                }
                delay(500)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
    }
    
    // Simple waveform generation (mocking for now, could be improved)
    fun generateWaveformData(duration: Long): List<Float> {
        return List(100) { Random.nextFloat() * 0.9f + 0.1f }
    }
}
