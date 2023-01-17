package com.hfad.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val fileName = getString(R.string.app_preference_file_name)
        val recentTracksListKey = getString(R.string.recent_tracks_list_key)
        val sharedPrefs = getSharedPreferences(fileName, MODE_PRIVATE)
        val searchHistory: SearchHistory
        val currentTrack:Track
        val btnBack = findViewById<ImageButton>(R.id.player_back_button)

        val trackNameTitle = findViewById<TextView>(R.id.playerTrackName)
        val trackArtist = findViewById<TextView>(R.id.playerTrackArtist)
        val trackDuration = findViewById<TextView>(R.id.playerTrackDuration)
        val trackNameTwo = findViewById<TextView>(R.id.playerTrackNameTwo)
        val trackYear = findViewById<TextView>(R.id.playerTrackYear)
        val trackGenre = findViewById<TextView>(R.id.playerTrackGenre)
        val trackCountry = findViewById<TextView>(R.id.playerTrackCountry)

        searchHistory = SearchHistory(sharedPrefs, recentTracksListKey)
        searchHistory.loadFromFile()
        currentTrack = searchHistory.recentTrack

        trackNameTitle.setText(currentTrack.trackName)
        trackArtist.setText(currentTrack.artistName)
        trackDuration.setText(currentTrack.trackTimeMillis)
        trackNameTwo.setText(currentTrack.collectionName)
        trackYear.setText(currentTrack.releaseDate)
        trackGenre.setText(currentTrack.primaryGenreName)
        trackCountry.setText(currentTrack.country)








        //Обрабатываем нажатие на кнопку "Назад"
        btnBack.setOnClickListener {
            onBackPressed()
        }



    }




}