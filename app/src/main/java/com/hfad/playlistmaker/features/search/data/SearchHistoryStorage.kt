package com.hfad.playlistmaker.features.search.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.hfad.playlistmaker.features.player.domain.models.Track
import com.hfad.playlistmaker.features.search.domain.api.Observer
import com.hfad.playlistmaker.features.search.domain.api.TrackStorage
import java.util.*
import kotlin.collections.ArrayList


class SearchHistoryStorage(
    private val file: SharedPreferences,
    private val key: String
) : TrackStorage, Observer {

    var recentTracksList = ArrayList<Track>()

    override fun addTrackToStorage(track: Track) {
        for (i in recentTracksList) {
            if (i.trackId == track.trackId) {
                recentTracksList.remove(i)
                break
            }
        }
        recentTracksList.add(track)

        if (recentTracksList.size > 10) {
            recentTracksList.removeAt(10)
        }
    }

    override fun clearStorage() {
        recentTracksList.clear()
        saveToFile()
    }

    fun loadFromFile() {
        val json: String = file.getString(key, null) ?: return
        recentTracksList.addAll(Gson().fromJson(json, Array<Track>::class.java))

    }

    fun saveToFile() {
        val json = Gson().toJson(recentTracksList)
        file.edit()
            .putString(key, json)
            .apply()
    }
}