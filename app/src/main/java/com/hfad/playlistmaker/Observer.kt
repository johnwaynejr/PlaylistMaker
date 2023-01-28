package com.hfad.playlistmaker

interface Observer {
    fun addTrackToRecentList(track: Track)
    fun saveToFile()
}