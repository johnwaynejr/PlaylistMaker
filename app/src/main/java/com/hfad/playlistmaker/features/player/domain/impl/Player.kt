package com.hfad.playlistmaker.features.player.domain.impl

import android.media.MediaPlayer
import android.os.Handler
import com.hfad.playlistmaker.features.player.presentation.PlayerView


class Player(
    val mediaPlayer: MediaPlayer,
    val playerView: PlayerView,
    val url: String,
    val handler: Handler
) {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3
        const val TIME_ELAPSED_DELAY = 1000L
    }

    var playerState = STATE_DEFAULT

    fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerView.enablePlayButton(true)
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            playerView.setPlayButton(true)
            playerState = STATE_PREPARED
            playerView.setTimeElapse("00:00")
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerView.setPlayButton(false)
        playerState = STATE_PLAYING
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        playerView.setPlayButton(true)
        playerState = STATE_PAUSED
        handler.removeCallbacks({ playerState == STATE_PAUSED })
    }

    fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

}