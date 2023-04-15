package com.hfad.playlistmaker.features.search.presentation

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.util.Creator
import com.hfad.playlistmaker.features.search.domain.api.TrackInteractor
import com.hfad.playlistmaker.features.search.domain.models.Track
import com.hfad.playlistmaker.features.search.ui.models.SearchState


class TrackSearchViewModel(application: Application): AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TrackSearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private var latestSearchText: String? = null

    private val trackInteractor = Creator.provideTrackInteractor(getApplication())
    private val handler = Handler(Looper.getMainLooper())
    private val tracks = ArrayList<Track>()

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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
    //Функция выполнения поискового запроса
    fun searchQuery(newSearchText: String) {

        if (newSearchText.isNotEmpty()) {

            renderState(SearchState.Loading)

            trackInteractor.search(newSearchText, object : TrackInteractor.TrackConsumer {
                 override fun consume(foundTracks: List<Track>?,errorMessage: String?) {

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
                })
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }


}