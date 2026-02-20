package com.codmeric.musicplayer.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.codmeric.musicplayer.MusicPlayerApp
import com.codmeric.musicplayer.R
import com.codmeric.musicplayer.databinding.FragmentPlayerBinding
import com.codmeric.musicplayer.viewmodel.SharedMediaViewModel
import com.codmeric.musicplayer.viewmodel.SharedMediaViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SharedMediaViewModel by activityViewModels {
        val app = requireActivity().application as MusicPlayerApp
        SharedMediaViewModelFactory(
            app.repository,
            app.mediaManager,
            app.equalizerManager,
            requireContext().applicationContext
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnPlayPause.setOnClickListener {
            val current = viewModel.currentTrack.value
            if (current == null) {
                viewModel.tracks.value.firstOrNull()?.let { viewModel.playTrack(it) }
            } else {
                viewModel.togglePlayPause()
            }
        }

        binding.btnNext.setOnClickListener { viewModel.skipNext() }
        binding.btnPrevious.setOnClickListener { viewModel.skipPrevious() }

        binding.btnEqualizer.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_playerFragment_to_equalizerFragment)
            } catch (e: Exception) {
                Log.e("MusicPlayer", "Navigation to equalizer failed", e)
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currentTrack.collect { track ->
                        if (_binding == null) return@collect
                        track?.let {
                            binding.tvSongTitle.text = it.title
                            binding.tvArtistAlbum.text = "${it.artist} — ${it.album}"
                            binding.seekBar.max = it.duration.toInt()
                            if (it.albumArt != null) {
                                binding.ivAlbumArt.setImageBitmap(it.albumArt)
                            } else {
                                binding.ivAlbumArt.setImageResource(R.drawable.ic_music_placeholder)
                            }
                            // Update waveform with new track data
                            binding.viewWaveform.updateWaveform(viewModel.getWaveformData())
                        }
                    }
                }

                launch {
                    viewModel.isPlaying.collect { isPlaying ->
                        if (_binding == null) return@collect
                        binding.btnPlayPause.setImageResource(
                            if (isPlaying) android.R.drawable.ic_media_pause
                            else android.R.drawable.ic_media_play
                        )
                    }
                }

                launch {
                    viewModel.currentPosition.collect { position ->
                        if (_binding == null) return@collect
                        binding.seekBar.progress = position
                        val duration = viewModel.currentTrack.value?.duration ?: 0L
                        binding.tvProgress.text = "${formatTime(position.toLong())} / ${formatTime(duration)}"
                        
                        // Update waveform progress
                        if (duration > 0) {
                            binding.viewWaveform.updateProgress(position.toFloat() / duration)
                        }
                    }
                }
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        return "%d:%02d".format(minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
