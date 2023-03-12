package com.hfad.playlistmaker.features.search.presentation

import com.hfad.playlistmaker.features.search.domain.TrackRecyclerAdapter
import com.hfad.playlistmaker.features.search.domain.models.Track

interface SearchView {

    fun showTrackList(isVisible: Boolean)

    fun showPlaceholderMessage(isVisible:Boolean)

    fun setPlaceholderMessage(messageNum:Int)

    fun showPlaceholderImage(isVisible:Boolean)

    fun setPlaceholderImage(imageNum:Int)

    fun showPlaceholderButton(isVisible:Boolean)

    fun setTextPlaceholderButton(textNum:Int)

    fun showHistoryTitle(isVisible:Boolean)

    fun showProgressBar(isVisible:Boolean)

    fun initAdapter()

    fun initAdapter(adapter: TrackRecyclerAdapter)

    fun updateTrackList(newTrackList:List<Track>)

    fun notifyAdapter()

    fun hideKeyboard()

}