package com.hfad.playlistmaker.features.search.domain.api

import com.hfad.playlistmaker.features.search.domain.models.Track

interface Observer {
    fun addTrackToStorage(track: Track)
}