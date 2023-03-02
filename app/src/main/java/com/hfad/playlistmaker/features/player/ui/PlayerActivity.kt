package com.hfad.playlistmaker.features.player.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.features.Creator


class PlayerActivity : AppCompatActivity() {


    private val playerController=Creator.providePlayerController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playerController.onCreate()

    }

    override fun onStart() {
        super.onStart()
        playerController.onStart()
    }

    override fun onPause() {
        super.onPause()
        playerController.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerController.onDestroy()

    }

}