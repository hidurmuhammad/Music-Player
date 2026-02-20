package com.codmeric.musicplayer

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.codmeric.musicplayer.databinding.ActivityMainBinding
import com.codmeric.musicplayer.viewmodel.SharedMediaViewModel
import com.codmeric.musicplayer.viewmodel.SharedMediaViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    
    // Shared ViewModel across fragments
    private val viewModel: SharedMediaViewModel by viewModels {
        val app = application as MusicPlayerApp
        SharedMediaViewModelFactory(
            app.repository,
            app.mediaManager,
            app.equalizerManager,
            applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
        } catch (e: Exception) {
            Log.e("MusicPlayer", "MainActivity onCreate ERROR", e)
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            if (viewModel.isPlaying.value) {
                viewModel.togglePlayPause()
            }
        } catch (e: Exception) {
            Log.e("MusicPlayer", "Error pausing in onStop", e)
        }
    }
}

