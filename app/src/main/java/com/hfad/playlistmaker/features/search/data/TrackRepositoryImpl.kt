package com.hfad.playlistmaker.features.search.data

import com.hfad.playlistmaker.features.search.data.dto.TrackSearchRequest
import com.hfad.playlistmaker.features.search.data.dto.TrackSearchResponse
import com.hfad.playlistmaker.features.search.domain.api.TrackRepository
import com.hfad.playlistmaker.features.search.domain.models.Track
import com.hfad.playlistmaker.util.Resource

class TrackRepositoryImpl(private val networkClient: NetworkClient,private val localStorage: LocalStorage) : TrackRepository {

    override fun search(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                val stored = localStorage.getSavedFavorites()

                Resource.Success((response as TrackSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.trackTimeMillis,
                        it.previewUrl,
                        it.artworkUrl100,
                        stored.contains(it.trackId)
                    )
                })
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }

    override fun addTrackToFavorites(track: Track) {
        localStorage.addToFavorites(track.trackId)
    }

    override fun removeTrackFromFavorites(track: Track) {
        localStorage.removeFromFavorites(track.trackId)
    }
}
