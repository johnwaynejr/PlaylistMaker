package com.hfad.playlistmaker.features.search.presentation

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.util.Creator
import com.hfad.playlistmaker.features.search.data.SearchHistoryStorage
import com.hfad.playlistmaker.features.search.domain.TrackRecyclerAdapter
import com.hfad.playlistmaker.features.search.domain.api.TrackInteractor
import com.hfad.playlistmaker.features.search.domain.models.Track

class TrackSearchPresenter(
    private val view: SearchView,
    private val context: Context,
    private val historyAdapter: TrackRecyclerAdapter,
    private val historyStorage: SearchHistoryStorage
) {
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
        view.initAdapter()
        view.showHistoryTitle(false)
        view.showPlaceholderButton(false)
        view.showTrackList(true)
        view.showPlaceholderImage(false)
        view.showPlaceholderMessage(false)
        view.hideKeyboard()
        view.showProgressBar(true)

        if (newSearchText.isNotEmpty()) {
            trackInteractor.search(newSearchText, object : TrackInteractor.TrackConsumer {
                 override fun consume(foundTracks: List<Track>?,errorMessage: String?) {
                        handler.post {
                            view.showProgressBar(false)
                           if(foundTracks!=null) {
                               tracks.clear()
                               tracks.addAll(foundTracks)
                               view.updateTrackList(tracks)
                               view.notifyAdapter()
                               view.showTrackList(true)

                           }
                            if (errorMessage != null) {
                                showQueryPlaceholder(R.drawable.finderror, R.string.something_went_wrong,true)
                                tracks.clear()
                            } else if (tracks.isEmpty()) {
                                showQueryPlaceholder(R.drawable.findnothing,R.string.nothing_found,false)
                            } else {
                                //
                            }
                        }

                        // showQueryPlaceholder(R.drawable.nointernet, R.string.no_internet,true)
                    }
                })
        }
    }

    fun showHistory() {
        view.showHistoryTitle(true)
        view.showPlaceholderButton(true)
        view.setTextPlaceholderButton(R.string.btn_clear_history)

        view.initAdapter(historyAdapter)
        view.notifyAdapter()
        view.showTrackList(true)

    }

        fun clearSearchingHistory() {
        view.showHistoryTitle(false)
        view.showPlaceholderButton(false)
        view.showTrackList(false)
        historyStorage.clearStorage()
    }

    // Функция отображения заглушки при неудачном поиске, скрытие списка треков и отображения кнопки "Обновить"
    private fun showQueryPlaceholder(imageNum: Int, messageNum: Int, btnStatus: Boolean){

        view.showPlaceholderImage(true)
        view.setPlaceholderImage(imageNum)
        view.showPlaceholderMessage(true)
        view.setPlaceholderMessage(messageNum)
        view.showTrackList(false)
        view.showPlaceholderButton(btnStatus)
        view.setTextPlaceholderButton(R.string.btn_update)
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