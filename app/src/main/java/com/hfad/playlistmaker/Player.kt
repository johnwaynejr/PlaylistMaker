package com.hfad.playlistmaker
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale


class Player(val play: ImageButton, val timeElapsed: TextView ,val url:String) : AppCompatActivity() {

    private var mediaPlayer = MediaPlayer()

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIME_ELAPSED_DELAY = 1000L
    }

    private val handler = Handler(Looper.getMainLooper())

    private var playerState = STATE_DEFAULT
    //var url = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview112/v4/ac/c7/d1/acc7d13f-6634-495f-caf6-491eccb505e8/mzaf_4002676889906514534.plus.aac.p.m4a"

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

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.btn_play)
        playerState = STATE_PAUSED
        handler.removeCallbacks({playerState == STATE_PAUSED})
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
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks({playerState >=0})
        mediaPlayer.release()

    }

    fun timeElapse(){
        var tElapsed = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
            mediaPlayer.duration-mediaPlayer.currentPosition)
        timeElapsed.text = tElapsed
    }

    override fun onStart() {
        super.onStart()

        handler?.postDelayed(
            object : Runnable {
                override fun run() {
                    // Обновляем список в главном потоке
                    if(playerState == STATE_PLAYING) {
                        timeElapse()
                    }
                    // И снова планируем то же действие через 1 секунду
                    handler?.postDelayed(this, TIME_ELAPSED_DELAY)
                }
            },TIME_ELAPSED_DELAY)
    }


}