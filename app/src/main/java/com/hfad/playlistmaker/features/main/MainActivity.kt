package com.hfad.playlistmaker.features.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.hfad.playlistmaker.features.search.ui.SearchActivity
import com.hfad.playlistmaker.features.media.MediaLibraryActivity
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.features.settings.domain.SetTheme
import com.hfad.playlistmaker.features.settings.ui.SettingsActivity
import com.hfad.playlistmaker.features.settings.ui.THEME_KEY
import com.hfad.playlistmaker.features.settings.ui.THEME_PREFERENCES


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPrefs = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE)
        var isDark = sharedPrefs.getBoolean(THEME_KEY, false)
        val settingsTheme = SetTheme()
        settingsTheme.switchTheme(isDark)

        val btnFind = findViewById<Button>(R.id.btn_find_am)
        btnFind.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
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