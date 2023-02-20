package com.hfad.playlistmaker.features.search

import com.hfad.playlistmaker.features.player.data.network.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<TrackResponse>
}