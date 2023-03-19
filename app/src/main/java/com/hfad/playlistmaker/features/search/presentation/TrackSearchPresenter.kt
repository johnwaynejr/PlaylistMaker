package com.hfad.playlistmaker.features.search.presentation

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.util.Creator
import com.hfad.playlistmaker.features.search.domain.api.TrackInteractor
import com.hfad.playlistmaker.features.search.domain.models.Track

class TrackSearchPresenter(
    private val view: SearchView,
    private val context: Context) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val trackInteractor = Creator.provideTrackInteractor(context)
    private val handler = Handler(Looper.getMainLooper())
    private val tracks = ArrayList<Track>()

    fun onDestroy() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    //Функция выполнения поискового запроса
    fun searchQuery(newSearchText: String) {

        if (newSearchText.isNotEmpty()) {
            view.initAdapter()
            view.showLoading()
            trackInteractor.search(newSearchText, object : TrackInteractor.TrackConsumer {
                 override fun consume(foundTracks: List<Track>?,errorMessage: String?) {
                        handler.post {
                            if (foundTracks != null) {
                                tracks.clear()
                                tracks.addAll(foundTracks)
                            }
                            when {
                                errorMessage != null -> {
                                    view.showError(
                                        R.drawable.finderror,
                                        R.string.something_went_wrong,
                                        true
                                    )
                                }
                                tracks.isEmpty() -> {
                                    view.showError(
                                        R.drawable.findnothing,
                                        R.string.nothing_found,
                                        false
                                    )
                                }
                                else -> {
                                    view.showContent(tracks)
                                }
                            }
                        }
                     }
                })
        }
    }

    fun searchDebounce(changedText:String) {

        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchQuery(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }
}