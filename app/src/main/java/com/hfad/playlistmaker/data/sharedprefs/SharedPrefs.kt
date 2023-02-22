package com.hfad.playlistmaker.data.sharedprefs

import com.google.gson.Gson
import android.content.SharedPreferences

// Заготовка для реализации SharedPrefs в отдельном классе
class SharedPrefs<T>(
    private val file: SharedPreferences,
    private val key: String, type: Class<Array<T>>
) {

    var objectsList = type


    /*fun loadFromFile():ArrayList<T> {
        val json: String = file.getString(key, null)
        return Gson().fromJson(json, type)
    }*/

    fun saveToFile() {
        val json = Gson().toJson(objectsList)
        file.edit()
            .putString(key, json)
            .apply()
    }

    fun clearFile() {
        // objectsList.clear()
        saveToFile()
    }
}