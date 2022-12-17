package com.hfad.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFind = findViewById<Button>(R.id.btn_find_am)
        btnFind.setOnClickListener {
            val displayIntent = Intent(this, FindActivity::class.java)
            startActivity(displayIntent)
        }

        val btnMedia = findViewById<Button>(R.id.btn_media_am)
        btnMedia.setOnClickListener {
            val displayIntent = Intent(this, MediaLibraryActivity::class.java)
            startActivity(displayIntent)
        }

        val btnSettings = findViewById<Button>(R.id.btn_settings_am)
        btnSettings.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }

    }
}