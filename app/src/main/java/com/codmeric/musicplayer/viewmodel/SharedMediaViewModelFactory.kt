package com.codmeric.musicplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codmeric.musicplayer.data.AudioRepository
import com.codmeric.musicplayer.player.EqualizerManager
import com.codmeric.musicplayer.player.MediaManager

import android.util.Log

class SharedMediaViewModelFactory(
    private val repository: AudioRepository,
    private val mediaManager: MediaManager,
    private val equalizerManager: EqualizerManager,
    private val context: android.content.Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedMediaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedMediaViewModel(repository, mediaManager, equalizerManager, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
