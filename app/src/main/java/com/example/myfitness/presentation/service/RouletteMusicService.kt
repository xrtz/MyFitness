package com.example.myfitness.presentation.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.myfitness.R

class RouletteMusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        releasePlayer()

        mediaPlayer = MediaPlayer.create(this, R.raw.roulette_music)?.apply {
            isLooping = false
            setVolume(MUSIC_VOLUME, MUSIC_VOLUME)
            setOnCompletionListener {
                stopSelf(startId)
            }
            setOnErrorListener { _, _, _ ->
                stopSelf(startId)
                true
            }
            start()
        }

        if (mediaPlayer == null) {
            stopSelf(startId)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun releasePlayer() {
        mediaPlayer?.let { player ->
            runCatching {
                if (player.isPlaying) player.stop()
            }
            player.release()
        }
        mediaPlayer = null
    }

    private companion object {
        const val MUSIC_VOLUME = 0.65f
    }
}
