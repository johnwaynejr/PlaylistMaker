package com.hfad.playlistmaker.features.player.domain.models

data class TrackModel(
    val id: String,
    val name: String,
    val author: String,
    val pictureUrl: String,
)

sealed class TrackScreenState {
    object Loading: TrackScreenState()
    data class Content(
        val trackModel: TrackModel,
    ): TrackScreenState()
}