package com.codmeric.musicplayer.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.codmeric.musicplayer.R

/**
 * A minimal fragment shown on startup with a single ProgressBar.
 * After the first frame is drawn, it navigates to the real PlayerFragment.
 */
class LoadingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Wait 100ms after the loading UI is visible before inflating the player
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToPlayer()
        }, 100)
    }

    private fun navigateToPlayer() {
        if (isAdded && !isDetached) {
            try {
                findNavController().navigate(R.id.action_loadingFragment_to_playerFragment)
            } catch (e: Exception) {
                Log.e("MusicPlayer", "LoadingFragment navigation error", e)
            }
        }
    }
}
