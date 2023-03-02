package com.hfad.playlistmaker.features

import android.app.Activity
import com.hfad.playlistmaker.features.player.presentation.PlayerController
import com.hfad.playlistmaker.features.search.data.SearchHistoryStorage
import com.hfad.playlistmaker.features.search.data.TrackRepositoryImpl
import com.hfad.playlistmaker.features.search.data.network.RetrofitNetworkClient
import com.hfad.playlistmaker.features.search.domain.TrackRecyclerAdapter
import com.hfad.playlistmaker.features.search.domain.api.TrackInteractor
import com.hfad.playlistmaker.features.search.domain.api.TrackRepository
import com.hfad.playlistmaker.features.search.domain.impl.TrackInteractorImpl
import com.hfad.playlistmaker.features.search.presentation.TrackSearchController

object Creator {

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideTrackSearchController(activity: Activity,
                                     adapter: TrackRecyclerAdapter,
                                     historyAdapter: TrackRecyclerAdapter,
                                     historyStorage: SearchHistoryStorage): TrackSearchController {
        return TrackSearchController(activity, adapter, historyAdapter,historyStorage)
    }

    fun providePlayerController(activity: Activity): PlayerController {
        return PlayerController(activity)
    }

}