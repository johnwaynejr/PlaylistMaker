package com.hfad.playlistmaker.features.search.domain.api

import com.hfad.playlistmaker.features.search.domain.models.Track

interface TrackRepository {
    fun search(expression: String): List<Track>
}