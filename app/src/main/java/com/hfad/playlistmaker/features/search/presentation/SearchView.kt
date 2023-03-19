package com.hfad.playlistmaker.features.search.presentation

import com.hfad.playlistmaker.features.search.domain.models.Track

interface SearchView {

    //Состояние «Загрузки»
    fun showLoading()

    // Состояние «Контента»
    fun showContent(tracks: ArrayList<Track>)

    // Состояние «Ошибки»
    fun showError(imageNum: Int, messageNum: Int, btnStatus: Boolean)

    // Состояние «История»
    fun showHistory(isVisible: Boolean)

    fun initAdapter()

    fun updateTrackList(newTrackList:List<Track>)


}