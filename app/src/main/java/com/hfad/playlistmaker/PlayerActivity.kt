package com.hfad.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val btnBack = findViewById<ImageButton>(R.id.player_back_button)

        //Обрабатываем нажатие на кнопку "Назад"
        btnBack.setOnClickListener {
            onBackPressed()
        }

    }




}