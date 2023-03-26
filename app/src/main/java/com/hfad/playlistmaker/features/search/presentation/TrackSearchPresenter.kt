package com.hfad.playlistmaker.features.search.presentation

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.util.Creator
import com.hfad.playlistmaker.features.search.domain.api.TrackInteractor
import com.hfad.playlistmaker.features.search.domain.models.Track
import com.hfad.playlistmaker.features.search.ui.models.SearchState

class TrackSearchPresenter(
    private val context: Context) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private var view: SearchView? = null
    private var state: SearchState? = null
    private var latestSearchText: String? = null

    fun attachView(view: SearchView) {
        this.view = view
        state?.let { view.render(it) }
    }

    fun detachView() {
        this.view = null
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
            view?.initAdapter()
            renderState(SearchState.Loading)

            trackInteractor.search(newSearchText, object : TrackInteractor.TrackConsumer {
                 override fun consume(foundTracks: List<Track>?,errorMessage: String?) {
                        handler.post {
                            if (foundTracks != null) {
                                tracks.clear()
                                tracks.addAll(foundTracks)
                            }
                            when {
                                errorMessage != null -> {
                                    renderState(
                                        SearchState.Error(
                                        R.drawable.finderror,
                                        R.string.something_went_wrong,
                                        true
                                        )
                                    )
                                }
                                tracks.isEmpty() -> {
                                    renderState(
                                        SearchState.Error(
                                        R.drawable.findnothing,
                                        R.string.nothing_found,
                                        false
                                        )
                                    )
                                }
                                else -> {
                                   renderState(
                                        SearchState.Content(tracks)
                                    )
                                }
                            }
                        }
                     }
                })
        }
    }

    private fun renderState(state: SearchState) {
        this.state = state
        this.view?.render(state)
    }

    fun searchDebounce(changedText:String) {

        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText

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