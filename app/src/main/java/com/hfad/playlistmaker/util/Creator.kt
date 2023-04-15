package com.hfad.playlistmaker.util

import android.content.Context
import com.hfad.playlistmaker.features.player.presentation.PlayerPresenter
import com.hfad.playlistmaker.features.player.presentation.PlayerView
import com.hfad.playlistmaker.features.search.data.TrackRepositoryImpl
import com.hfad.playlistmaker.features.search.data.network.RetrofitNetworkClient
import com.hfad.playlistmaker.features.search.domain.api.TrackInteractor
import com.hfad.playlistmaker.features.search.domain.api.TrackRepository
import com.hfad.playlistmaker.features.search.domain.impl.TrackInteractorImpl
import com.hfad.playlistmaker.features.search.domain.models.Track

object Creator {

    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    /*fun provideTrackSearchPresenter(
        context: Context): TrackSearchViewModel {
        return TrackSearchViewModel(context)
    }*/

    fun providePlayerPresenter(playerView: PlayerView,
                               currentTrack: Track): PlayerPresenter {
        return PlayerPresenter(playerView, currentTrack)
    }

}