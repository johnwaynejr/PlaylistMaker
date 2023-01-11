package com.hfad.playlistmaker

interface Observable {
    fun addObserver(observer: Observer)
}