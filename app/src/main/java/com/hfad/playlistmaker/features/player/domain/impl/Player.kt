package com.hfad.playlistmaker.features.player.domain.impl
import android.media.MediaPlayer
import android.os.Handler
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hfad.playlistmaker.R


class Player(val mediaPlayer:MediaPlayer,val play: ImageButton, val timeElapsed: TextView ,val url:String, val handler:Handler) : AppCompatActivity() {

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
            play.isEnabled = true
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.btn_play)
            playerState = STATE_PREPARED
            timeElapsed.text="00:00"
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.btn_pause)
        playerState = STATE_PLAYING
    }

     fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.btn_play)
        playerState = STATE_PAUSED
        handler.removeCallbacks({playerState == STATE_PAUSED })
    }
     fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

}