package com.hfad.playlistmaker.features

import android.app.Application
import android.content.Context


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        const val TRACK_KEY = "track"
        lateinit var appContext: Context
    }

}