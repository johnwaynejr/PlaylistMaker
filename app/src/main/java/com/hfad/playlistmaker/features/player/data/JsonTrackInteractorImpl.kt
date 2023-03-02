package com.hfad.playlistmaker.features.player.data

import com.google.gson.Gson
import com.hfad.playlistmaker.features.player.domain.api.JsonTrackInteractor
import com.hfad.playlistmaker.features.search.domain.models.Track

class JsonTrackInteractorImpl : JsonTrackInteractor {

    override fun getTrack(json: String): Track {
        val track = Gson().fromJson(json, Track::class.java)
        return track
    }
}