package com.codmeric.musicplayer.data

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioRepository(private val context: Context) {
    
    private val trackFiles = listOf("track1.mp3", "track2.mp3", "track3.mp3")

    suspend fun getTracks(): List<AudioTrack> = withContext(Dispatchers.IO) {
        val tracks = mutableListOf<AudioTrack>()
        val retriever = MediaMetadataRetriever()

        trackFiles.forEachIndexed { index, fileName ->
            try {
                val afd = context.assets.openFd(fileName)
                retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

                val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: fileName
                val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "Unknown Artist"
                val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "Unknown Album"
                val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
                
                val artBytes = retriever.embeddedPicture
                val bitmap = artBytes?.let { bytes ->
                    try {
                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                        
                        // Target size 300x300 for the UI
                        options.inSampleSize = calculateInSampleSize(options, 300, 300)
                        options.inJustDecodeBounds = false
                        
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                    } catch (e: Exception) {
                        Log.e("MusicPlayer", "Error decoding bitmap for $fileName", e)
                        null
                    }
                }
                
                tracks.add(AudioTrack(index.toString(), title, artist, album, duration, fileName, bitmap))
                afd.close()
            } catch (e: Exception) {
                e.printStackTrace()
                tracks.add(AudioTrack(index.toString(), fileName, "Unknown", "Unknown", 0L, fileName, null))
            }
        }
        try {
            retriever.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        tracks
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
