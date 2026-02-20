package com.codmeric.musicplayer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codmeric.musicplayer.data.AudioRepository
import com.codmeric.musicplayer.data.AudioTrack
import com.codmeric.musicplayer.player.EqualizerManager
import com.codmeric.musicplayer.player.MediaManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SharedMediaViewModel(
    private val repository: AudioRepository,
    private val mediaManager: MediaManager,
    private val equalizerManager: EqualizerManager,
    private val context: android.content.Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("music_player_prefs", android.content.Context.MODE_PRIVATE)

    private val _tracks = MutableStateFlow<List<AudioTrack>>(emptyList())
    val tracks: StateFlow<List<AudioTrack>> = _tracks

    val currentTrack = mediaManager.currentTrack
    val isPlaying = mediaManager.isPlaying
    val currentPosition = mediaManager.currentPosition

    private val _selectedPreset = MutableStateFlow(prefs.getString("selected_preset", "Flat") ?: "Flat")

    init {
        loadTracks()
    }

    private fun loadTracks() {
        viewModelScope.launch {
            try {
                _tracks.value = repository.getTracks()
                if (_tracks.value.isNotEmpty()) {
                    // Pre-select the first track in the UI without playing it
                    mediaManager.setCurrentTrack(_tracks.value[0])
                }
            } catch (e: Exception) {
                Log.e("MusicPlayer", "SharedMediaViewModel loadTracks ERROR", e)
            }
        }
    }

    fun playTrack(track: AudioTrack) {
        mediaManager.playTrack(track)
        equalizerManager.initEqualizer(mediaManager.getAudioSessionId())
        equalizerManager.applyPreset(_selectedPreset.value)
    }

    fun togglePlayPause() {
        val current = currentTrack.value
        if (current != null && !mediaManager.isMediaPlayerReady()) {
            playTrack(current)
        } else {
            mediaManager.togglePlayPause()
        }
    }

    fun seekTo(position: Int) {
        mediaManager.seekTo(position)
    }

    fun skipNext() {
        val current = currentTrack.value ?: return
        val index = _tracks.value.indexOf(current)
        if (index < _tracks.value.size - 1) {
            playTrack(_tracks.value[index + 1])
        }
    }

    fun skipPrevious() {
        val current = currentTrack.value ?: return
        val index = _tracks.value.indexOf(current)
        if (index > 0) {
            playTrack(_tracks.value[index - 1])
        }
    }

    fun setPreset(preset: String) {
        _selectedPreset.value = preset
        prefs.edit().putString("selected_preset", preset).apply()
        equalizerManager.applyPreset(preset)
    }

    fun setBandLevel(band: Short, level: Short) {
        equalizerManager.setBandLevel(band, level)
    }

    fun getBandLevel(band: Short): Short = equalizerManager.getBandLevel(band)

    fun getWaveformData(): List<Float> {
        return mediaManager.generateWaveformData(currentTrack.value?.duration ?: 0L)
    }

    fun setBassLevel(level: Float) {
        val millibel = (level * 1000).toInt().toShort()
        equalizerManager.setBassLevel(millibel)
        prefs.edit().putFloat("bass_level", level).apply()
    }

    fun setTrebleLevel(level: Float) {
        val millibel = (level * 1000).toInt().toShort()
        equalizerManager.setVirtualizerLevel(millibel)
        prefs.edit().putFloat("treble_level", level).apply()
    }

    fun getBassLevel(): Float = prefs.getFloat("bass_level", 0f)
    fun getTrebleLevel(): Float = prefs.getFloat("treble_level", 0f)

    fun setEqualizerEnabled(enabled: Boolean) {
        equalizerManager.setEffectsEnabled(enabled)
        prefs.edit().putBoolean("eq_enabled", enabled).apply()
    }

    fun isEqualizerEnabled(): Boolean = equalizerManager.isEnabled()

    override fun onCleared() {
        super.onCleared()
        mediaManager.release()
        equalizerManager.release()
    }
}
