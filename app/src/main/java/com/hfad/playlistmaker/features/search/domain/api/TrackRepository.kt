package com.hfad.playlistmaker.features.search.domain.api

import com.hfad.playlistmaker.features.search.domain.models.Track
import com.hfad.playlistmaker.util.Resource

interface TrackRepository {
    fun search(expression: String): Resource<List<Track>>
}