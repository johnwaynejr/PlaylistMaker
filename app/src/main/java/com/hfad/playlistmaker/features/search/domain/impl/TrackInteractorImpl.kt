package com.hfad.playlistmaker.features.search.domain.impl

import com.hfad.playlistmaker.features.search.domain.api.TrackInteractor
import com.hfad.playlistmaker.features.search.domain.api.TrackRepository
import com.hfad.playlistmaker.features.search.domain.models.Track
import com.hfad.playlistmaker.util.Resource
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

   private val executor = Executors.newCachedThreadPool()

    override fun search(expression: String, consumer: TrackInteractor.TrackConsumer) {
        executor.execute {
        when(val resource = repository.search(expression)) {
            is Resource.Success -> {
                consumer.consume(resource.data, null)
            }
            is Resource.Error -> {
                consumer.consume(null, resource.message)
            }
        }
      }
    }

    override fun addTrackToFavorites(track: Track) {
        repository.addTrackToFavorites(track)
    }

    override fun removeTrackToFavorites(track: Track) {
        repository.removeTrackFromFavorites(track)
    }


}