package com.hfad.playlistmaker.features.settings.domain

import androidx.appcompat.app.AppCompatDelegate

class SetTheme {

    fun switchTheme(darkThemeEnabled: Boolean) {

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
                }
            )
         }
    }