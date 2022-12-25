package com.hfad.playlistmaker
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track (
    val trackName: String,  // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long?, // Продолжительность трека
    val artworkUrl100: String):Parcelable // Ссылка на изображение обложки