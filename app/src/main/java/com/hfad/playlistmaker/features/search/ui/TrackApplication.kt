package com.hfad.playlistmaker.features.search.ui

import android.app.Application
import com.hfad.playlistmaker.features.search.presentation.TrackSearchViewModel

class TrackApplication:Application() {

    var trackSearchViewModel: TrackSearchViewModel? = null
}