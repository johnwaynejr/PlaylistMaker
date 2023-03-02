package com.hfad.playlistmaker.features.player.presentation

import android.app.Activity
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.features.player.data.JsonTrackInteractorImpl
import com.hfad.playlistmaker.features.player.domain.impl.Player
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerController(private val activity: Activity) {

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var trackNameTitle: TextView
    private lateinit var trackArtist: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var timeElapsed: TextView
    private lateinit var mediaPlayer:MediaPlayer
    private lateinit var player: Player

    fun onCreate() {

        val btnBack = activity.findViewById<ImageButton>(R.id.player_back_button)
        val trackCover = activity.findViewById<ImageView>(R.id.playerTrackCover)
        val playBtn = activity.findViewById<ImageButton>(R.id.playerPlayBtn)
        timeElapsed = activity.findViewById(R.id.playerTrackDurationLive)

        initVariables()

        //Получаем данные о выбранном треке
        val jsonT = activity.intent.getStringExtra(R.string.track_intent_key.toString())
        val jsonInteractor = JsonTrackInteractorImpl()
        val currentTrack = jsonInteractor.getTrack(jsonT!!)

        val bigCover = currentTrack.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
        val longTrackAlbum = currentTrack.collectionName
        val shortTrackAlbum =
            if (longTrackAlbum.length < 40) longTrackAlbum else longTrackAlbum.substring(0, 39)

        val shortDate = currentTrack.releaseDate.substring(0, 4)

        mediaPlayer=MediaPlayer()

        //Подгтовка плеера
        val trackURl = currentTrack.previewUrl
        player = Player(mediaPlayer, playBtn, timeElapsed, trackURl, handler)
        player.preparePlayer()

        // Переносим данные на экран плеера
        Glide.with(activity)
            .load(bigCover)
            .placeholder(R.drawable.findnothing)
            .fitCenter()
            .transform(RoundedCorners(10))
            .into(trackCover)

        trackNameTitle.setText(currentTrack.trackName)
        trackArtist.setText(currentTrack.artistName)
        trackDuration.setText(formatTrackDuration(currentTrack.trackTimeMillis))
        trackAlbum.setText(shortTrackAlbum)
        trackYear.setText(shortDate)
        trackGenre.setText(currentTrack.primaryGenreName)
        trackCountry.setText(currentTrack.country)

        //Обрабатываем нажатие на кнопку "Назад"
        btnBack.setOnClickListener {
            activity.onBackPressed()
        }
        //Обрабатываем нажатие кнопки Play
        playBtn.setOnClickListener {
            player.playbackControl()
            timeElapse()
        }

    }
    private fun initVariables() {
        trackNameTitle = activity.findViewById(R.id.playerTrackName)
        trackArtist = activity.findViewById(R.id.playerTrackArtist)
        trackDuration = activity.findViewById(R.id.playerTrackDuration)
        trackAlbum = activity.findViewById(R.id.playerTrackAlbum)
        trackYear = activity.findViewById(R.id.playerTrackYear)
        trackGenre = activity.findViewById(R.id.playerTrackGenre)
        trackCountry = activity.findViewById(R.id.playerTrackCountry)

    }

    private fun formatTrackDuration(timeTrack: String?): String {
        if (timeTrack != null) {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeTrack.toInt())
        }
        return "нет данных"
    }

fun onStart(){
    handler?.postDelayed(
        object : Runnable {
            override fun run() {
                // Обновляем список в главном потоке
                if (player.playerState == Player.STATE_PLAYING) {
                    timeElapse()
                }
                // И снова планируем то же действие через 1 секунду
                handler?.postDelayed(this, Player.TIME_ELAPSED_DELAY)
            }
        }, Player.TIME_ELAPSED_DELAY
    )
}
fun onPause(){
    player.pausePlayer()
}

fun onDestroy(){
    handler.removeCallbacks({ player.playerState >= 0 })
    mediaPlayer.release()
}
    fun timeElapse() {
        val tElapsed = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
            mediaPlayer.duration - mediaPlayer.currentPosition
        )
        timeElapsed.text = tElapsed
    }

}