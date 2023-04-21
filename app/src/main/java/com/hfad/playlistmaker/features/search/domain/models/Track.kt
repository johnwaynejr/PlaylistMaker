package com.hfad.playlistmaker.features.search.domain.models


data class Track(
    val trackId: String,
    val trackName: String,  // Название композиции
    val artistName: String, // Имя исполнителя
    val collectionName: String, //   Название альбома
    val releaseDate: String, //Год релиза трека
    val primaryGenreName: String, //Жанр трека
    val country: String, //Страна исполнителя
    val trackTimeMillis: String?, // Продолжительность трека
    val previewUrl: String, //Превью трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val inFavorite: Boolean //Избранное
)