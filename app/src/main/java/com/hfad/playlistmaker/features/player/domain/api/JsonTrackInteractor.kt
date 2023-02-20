package com.hfad.playlistmaker.features.player.domain.api
import android.content.Intent
import com.hfad.playlistmaker.features.player.domain.models.Track

interface JsonTrackInteractor {

    fun getTrack(json:String):Track

    fun putTrack()
}