package com.hfad.playlistmaker.features.search.domain.api

interface Observable {
    fun addObserver(observer: Observer)
}