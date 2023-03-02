package com.hfad.playlistmaker.features.search.data

import com.hfad.playlistmaker.features.search.data.dto.TrackSearchRequest
import com.hfad.playlistmaker.features.search.data.dto.TrackSearchResponse
import com.hfad.playlistmaker.features.search.domain.api.TrackRepository
import com.hfad.playlistmaker.features.search.domain.models.Track

class TrackRepositoryImpl (private val networkClient: NetworkClient) : TrackRepository {

        override fun search(expression: String): List<Track> {
            val response = networkClient.doRequest(TrackSearchRequest(expression))
            if (response.resultCode == 200) {
                return (response as TrackSearchResponse).results.map {
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
                        it.artworkUrl100) }
            } else {
                return emptyList()
            }
        }
    }
