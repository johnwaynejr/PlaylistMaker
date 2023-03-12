package com.hfad.playlistmaker.features.search.ui


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.features.Creator
import com.hfad.playlistmaker.features.player.ui.PlayerActivity
import com.hfad.playlistmaker.features.search.data.SearchHistoryStorage
import com.hfad.playlistmaker.features.search.domain.TrackRecyclerAdapter
import com.hfad.playlistmaker.features.search.presentation.TrackSearchController


class SearchActivity : AppCompatActivity() {

   companion object {
        private const val ET_VALUE = "ET_VALUE"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }


    private lateinit var searchHistoryStorage:SearchHistoryStorage
    private lateinit var trackSearchController:TrackSearchController

    private var adapter = TrackRecyclerAdapter{
        if(clickDebounce()) {
            searchHistoryStorage.addTrackToStorage(it)
            searchHistoryStorage.saveToFile()
            val intent = Intent(this, PlayerActivity::class.java)
            val json = Gson().toJson(it)
            intent.putExtra(R.string.track_intent_key.toString(), json)
            startActivity(intent)
        }
    }

    private var  historyAdapter = TrackRecyclerAdapter{
        if(clickDebounce()) {
            val intent = Intent(this, PlayerActivity::class.java)
            val json = Gson().toJson(it)
            intent.putExtra(R.string.track_intent_key.toString(), json)
            startActivity(intent)
        }
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val inputEditText = findViewById<EditText>(R.id.et_find)
        val strValET=inputEditText.text.toString()
        outState.putString(ET_VALUE,strValET)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)
        val fileName = getString(R.string.app_preference_file_name)
        val recentTracksListKey = getString(R.string.recent_tracks_list_key)
        val sharedPrefs = getSharedPreferences(fileName, MODE_PRIVATE)
        searchHistoryStorage=SearchHistoryStorage(sharedPrefs,recentTracksListKey)
        trackSearchController= Creator.provideTrackSearchController(this,adapter,historyAdapter,searchHistoryStorage)
        trackSearchController.onCreate()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val inputEditText = findViewById<EditText>(R.id.et_find)
        val strValET=savedInstanceState.getString(ET_VALUE)
        inputEditText.setText(strValET)
    }
    override fun onDestroy() {
        super.onDestroy()
        trackSearchController.onDestroy()
    }
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}


