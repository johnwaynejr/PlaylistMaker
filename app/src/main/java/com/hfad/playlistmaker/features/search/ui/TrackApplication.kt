package com.hfad.playlistmaker.features.search.ui

import android.app.Application
import com.hfad.playlistmaker.features.search.presentation.TrackSearchPresenter

class TrackApplication:Application() {

    var trackSearchPresenter: TrackSearchPresenter? = null
}