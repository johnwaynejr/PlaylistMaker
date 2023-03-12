package com.hfad.playlistmaker.features.player.presentation

interface PlayerView {

    fun setTrackPoster(url: String)

    fun setTrackName(string: String)

    fun setTrackArtist(string: String)

    fun setTrackDuration(string: String)

    fun setTrackAlbum(string: String)

    fun setTrackYear(string: String)

    fun setTrackGenre(string: String)

    fun setTrackCountry(string: String)

    fun enablePlayButton(isEnable:Boolean)

    fun setPlayButton(isPlay:Boolean)

    fun setTimeElapse(string: String)

}