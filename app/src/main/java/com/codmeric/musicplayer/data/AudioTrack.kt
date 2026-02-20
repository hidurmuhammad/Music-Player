package com.codmeric.musicplayer.data

import android.graphics.Bitmap

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val assetPath: String,
    val albumArt: Bitmap? = null
)
