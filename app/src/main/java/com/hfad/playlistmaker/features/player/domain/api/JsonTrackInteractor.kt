package com.hfad.playlistmaker.features.player.domain.api

import com.hfad.playlistmaker.features.search.domain.models.Track

interface JsonTrackInteractor {

    fun getTrack(json: String): Track
}