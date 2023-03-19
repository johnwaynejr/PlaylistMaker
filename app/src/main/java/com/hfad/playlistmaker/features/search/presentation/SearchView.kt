package com.hfad.playlistmaker.features.search.presentation

import com.hfad.playlistmaker.features.search.domain.models.Track
import com.hfad.playlistmaker.features.search.ui.models.SearchState

interface SearchView {

    fun render(state:SearchState)

    fun initAdapter()

    fun updateTrackList(newTrackList:List<Track>)


}