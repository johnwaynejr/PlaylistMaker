package com.hfad.playlistmaker.util

import android.content.Context
import com.hfad.playlistmaker.features.player.presentation.PlayerPresenter
import com.hfad.playlistmaker.features.player.presentation.PlayerView
import com.hfad.playlistmaker.features.search.data.SearchHistoryStorage
import com.hfad.playlistmaker.features.search.data.TrackRepositoryImpl
import com.hfad.playlistmaker.features.search.data.network.RetrofitNetworkClient
import com.hfad.playlistmaker.features.search.domain.TrackRecyclerAdapter
import com.hfad.playlistmaker.features.search.domain.api.TrackInteractor
import com.hfad.playlistmaker.features.search.domain.api.TrackRepository
import com.hfad.playlistmaker.features.search.domain.impl.TrackInteractorImpl
import com.hfad.playlistmaker.features.search.domain.models.Track
import com.hfad.playlistmaker.features.search.presentation.SearchView
import com.hfad.playlistmaker.features.search.presentation.TrackSearchPresenter

object Creator {

    private fun getTrackRepository(context: Context): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTrackInteractor(context: Context): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository(context))
    }

    fun provideTrackSearchPresenter(
        searchView: SearchView,
        context: Context,
        historyAdapter: TrackRecyclerAdapter,
        historyStorage: SearchHistoryStorage
    ): TrackSearchPresenter {
        return TrackSearchPresenter(searchView,context, historyAdapter, historyStorage)
    }

    fun providePlayerPresenter(playerView: PlayerView,
                               currentTrack: Track): PlayerPresenter {
        return PlayerPresenter(playerView, currentTrack)
    }

}