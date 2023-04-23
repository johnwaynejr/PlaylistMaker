package com.hfad.playlistmaker.features.player.presentation

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.hfad.playlistmaker.features.player.domain.impl.Player
import com.hfad.playlistmaker.features.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerPresenter(private val playerView: PlayerView, private val currentTrack: Track) {

    private lateinit var mediaPlayer: MediaPlayer
    lateinit var player: Player

    private val handler = Handler(Looper.getMainLooper())

    fun onCreate() {

        val longTrackAlbum = currentTrack.collectionName
        val shortTrackAlbum =
            if (longTrackAlbum.length < 40) longTrackAlbum else longTrackAlbum.substring(0, 39)
        val shortDate = currentTrack.releaseDate.substring(0, 4)
        val bigCover = currentTrack.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

        playerView.setTrackPoster(bigCover)
        playerView.setTrackName(currentTrack.trackName)
        playerView.setTrackArtist(currentTrack.artistName)
        playerView.setTrackDuration(formatTrackDuration(currentTrack.trackTimeMillis))
        playerView.setTrackAlbum(shortTrackAlbum)
        playerView.setTrackYear(shortDate)
        playerView.setTrackGenre(currentTrack.primaryGenreName)
        playerView.setTrackCountry(currentTrack.country)

        mediaPlayer = MediaPlayer()
        val trackURl = currentTrack.previewUrl
        player = Player(mediaPlayer, playerView, trackURl, handler)
        player.preparePlayer()

    }


    private fun formatTrackDuration(timeTrack: String?): String {
        if (timeTrack != null) {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeTrack.toInt())
        }
        return "нет данных"
    }

    fun onStart() {
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

    fun onPause() {
        player.pausePlayer()
    }

    fun onDestroy() {
        handler.removeCallbacks({ player.playerState >= 0 })
        mediaPlayer.release()
    }

    fun timeElapse() {
        val tElapsed = SimpleDateFormat("mm:ss", Locale.getDefault()).format(
            mediaPlayer.duration - mediaPlayer.currentPosition
        )
        playerView.setTimeElapse(tElapsed)
    }

}