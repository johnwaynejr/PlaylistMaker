package com.hfad.playlistmaker.features.player.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.features.player.data.JsonTrackInteractorImpl
import com.hfad.playlistmaker.features.player.presentation.PlayerPresenter
import com.hfad.playlistmaker.features.player.presentation.PlayerView
import com.hfad.playlistmaker.util.Creator


class PlayerActivity : AppCompatActivity(), PlayerView {

    private lateinit var trackNameTitle: TextView
    private lateinit var trackCover: ImageView
    private lateinit var trackArtist: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var playButton: ImageButton
    private lateinit var timeElapsed: TextView
    private lateinit var playerPresenter: PlayerPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val btnBack = findViewById<ImageButton>(R.id.player_back_button)

        //Получаем данные о выбранном треке
        val jsonT = intent.getStringExtra(R.string.track_intent_key.toString())
        val jsonInteractor = JsonTrackInteractorImpl()
        val currentTrack = jsonInteractor.getTrack(jsonT!!)

        initVariables()

        playerPresenter = Creator.providePlayerPresenter(this, currentTrack)
        playerPresenter.onCreate()



        //Обрабатываем нажатие на кнопку "Назад"
        btnBack.setOnClickListener {
            onBackPressed()
        }
        //Обрабатываем нажатие кнопки Play
        playButton.setOnClickListener {
            playerPresenter.player.playbackControl()
            playerPresenter.timeElapse()
        }

    }

    private fun initVariables() {
        trackNameTitle = findViewById(R.id.playerTrackName)
        trackCover = findViewById(R.id.playerTrackCover)
        trackArtist = findViewById(R.id.playerTrackArtist)
        trackDuration = findViewById(R.id.playerTrackDuration)
        trackAlbum = findViewById(R.id.playerTrackAlbum)
        trackYear = findViewById(R.id.playerTrackYear)
        trackGenre = findViewById(R.id.playerTrackGenre)
        trackCountry = findViewById(R.id.playerTrackCountry)
        playButton =findViewById(R.id.playerPlayBtn)
        timeElapsed = findViewById(R.id.playerTrackDurationLive)
    }


    override fun onStart() {
        super.onStart()
        playerPresenter.onStart()
    }

    override fun onPause() {
        super.onPause()
        playerPresenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerPresenter.onDestroy()

    }

    override fun setTrackPoster(url: String) {
        // Переносим данные на экран плеера
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.findnothing)
            .fitCenter()
            .transform(RoundedCorners(10))
            .into(trackCover)
    }
    override fun setTrackName(string: String) {
        trackNameTitle.setText(string)
    }
    override fun setTrackArtist(string: String) {
        trackArtist.setText(string)
    }
    override fun setTrackDuration(string: String) {
        trackDuration.setText(string)
    }
    override fun setTrackAlbum(string: String) {
        trackAlbum.setText(string)
    }
    override fun setTrackYear(string: String) {
        trackYear.setText(string)
    }
    override fun setTrackGenre(string: String) {
        trackGenre.setText(string)
    }
    override fun setTrackCountry(string: String) {
        trackCountry.setText(string)
    }

    override fun enablePlayButton(isEnable: Boolean) {
        playButton.isEnabled= isEnable
    }

    override fun setPlayButton(isPlay:Boolean) {
        if(isPlay) playButton.setImageResource(R.drawable.btn_play)
        else playButton.setImageResource(R.drawable.btn_pause)
    }

    override fun setTimeElapse(string: String) {
        timeElapsed.text=string
    }
}