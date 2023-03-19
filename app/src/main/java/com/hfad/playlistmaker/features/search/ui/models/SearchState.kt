package com.hfad.playlistmaker.features.search.ui.models

import com.hfad.playlistmaker.features.search.domain.models.Track

sealed interface SearchState {

    object Loading : SearchState

    data class Content(
        val tracks: ArrayList<Track>
    ) : SearchState

    data class Error(
        val imageNum: Int,
        val messageNum: Int,
        val btnStatus: Boolean
    ) : SearchState

    data class History(
        val isVisible: Boolean
    ) : SearchState

}