package com.hfad.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerActivity : AppCompatActivity() {

    private lateinit var trackNameTitle:TextView
    private lateinit var trackArtist:TextView
    private lateinit var trackDuration:TextView
    private lateinit var trackAlbum:TextView
    private lateinit var trackYear:TextView
    private lateinit var trackGenre:TextView
    private lateinit var trackCountry:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val fileName = getString(R.string.app_preference_file_name)
        val recentTracksListKey = getString(R.string.recent_tracks_list_key)
        val sharedPrefs = getSharedPreferences(fileName, MODE_PRIVATE)
        val btnBack = findViewById<ImageButton>(R.id.player_back_button)

        val trackCover = findViewById<ImageView>(R.id.playerTrackCover)

        //clearTrackData()
        initVariables()

        val searchHistory = SearchHistory(sharedPrefs, recentTracksListKey)
        searchHistory.loadFromFile()
        val currentTrack = searchHistory.recentTracksList.last()

        val smallCover = currentTrack.artworkUrl100
        fun getCoverArtwork() = smallCover.replaceAfterLast('/',"512x512bb.jpg")
        val bigCover=getCoverArtwork()

        val longTrackAlbum = currentTrack.collectionName
        val shortTrackAlbum = if (longTrackAlbum.length<30) longTrackAlbum else longTrackAlbum.substring(0,29)

        val longDate = currentTrack.releaseDate
        val shortDate = longDate.substring(0,4)
  // Переносим данные на экран плеера
        Glide.with(this)
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
            onBackPressed()
        }
    }

  private fun clearTrackData(){
        trackNameTitle.setText("")
        trackArtist.setText("")
        trackDuration.setText("")
        trackAlbum.setText("")
        trackYear.setText("")
        trackGenre.setText("")
        trackCountry.setText("")
    }

    private fun initVariables(){
        trackNameTitle = findViewById(R.id.playerTrackName)
        trackArtist = findViewById(R.id.playerTrackArtist)
        trackDuration = findViewById(R.id.playerTrackDuration)
        trackAlbum = findViewById(R.id.playerTrackAlbum)
        trackYear = findViewById(R.id.playerTrackYear)
        trackGenre = findViewById(R.id.playerTrackGenre)
        trackCountry = findViewById(R.id.playerTrackCountry)
    }

    private fun formatTrackDuration(timeTrack: String?): String {
        if (timeTrack != null) {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeTrack.toInt())
        }

        return "нет данных"
    }



}