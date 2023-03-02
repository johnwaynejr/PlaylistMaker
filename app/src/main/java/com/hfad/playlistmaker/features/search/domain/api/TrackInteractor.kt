package com.hfad.playlistmaker.features.search.domain.api

import com.hfad.playlistmaker.features.search.domain.models.Track

interface TrackInteractor {
    fun search(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTracks: List<Track>)
    }
}