package com.hfad.playlistmaker


data class Track (
    val trackId: String,
    val trackName: String,  // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: String?, // Продолжительность трека
    val artworkUrl100: String)// Ссылка на изображение обложки