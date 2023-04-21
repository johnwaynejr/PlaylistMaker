package com.hfad.playlistmaker.features.search.data

import android.content.SharedPreferences

class LocalStorage(private val sharedPreferences: SharedPreferences) {
    private companion object {
        const val FAVORITES_KEY = "FAVORITES_KEY"
    }

    fun addToFavorites(trackId: String) {
        changeFavorites(trackId=trackId, remove = false)
    }

    fun removeFromFavorites(trackId: String) {
        changeFavorites(trackId = trackId, remove = true)
    }

    fun getSavedFavorites(): Set<String> {
        return sharedPreferences.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
    }

    private fun changeFavorites(trackId: String, remove: Boolean) {
        val mutableSet = getSavedFavorites().toMutableSet()
        val modified = if (remove) mutableSet.remove(trackId) else mutableSet.add(trackId)
        if (modified) sharedPreferences.edit().putStringSet(FAVORITES_KEY, mutableSet).apply()
    }
}