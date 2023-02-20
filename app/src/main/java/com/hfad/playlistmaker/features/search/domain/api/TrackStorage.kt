package com.hfad.playlistmaker.features.search.domain.api

import com.hfad.playlistmaker.features.player.domain.models.Track

interface TrackStorage {
    fun addTrackToStorage(track: Track)
    fun clearStorage()
}