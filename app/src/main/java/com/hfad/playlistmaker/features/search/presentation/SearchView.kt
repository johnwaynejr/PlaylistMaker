package com.hfad.playlistmaker.features.search.presentation

import com.hfad.playlistmaker.features.search.ui.models.SearchState
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

interface SearchView: MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun render(state:SearchState)

}