package com.hfad.playlistmaker.features.search.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.features.player.ui.PlayerActivity
import com.hfad.playlistmaker.features.search.data.SearchHistoryStorage
import com.hfad.playlistmaker.features.search.domain.TrackRecyclerAdapter
import com.hfad.playlistmaker.features.search.domain.models.Track
import com.hfad.playlistmaker.features.search.presentation.TrackSearchViewModel
import com.hfad.playlistmaker.features.search.ui.models.SearchState


class SearchActivity : ComponentActivity() {

    companion object {
        private const val ET_VALUE = "ET_VALUE"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var adapter = TrackRecyclerAdapter {
        object : TrackRecyclerAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                if (clickDebounce()) {
                    searchHistoryStorage.addTrackToStorage(this@SearchActivity)
                    searchHistoryStorage.saveToFile()
                    val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
                    val json = Gson().toJson(this@SearchActivity)
                    intent.putExtra(R.string.track_intent_key.toString(), json)
                    startActivity(intent)
                }
            }
        }
    }

    private var historyAdapter = TrackRecyclerAdapter {
        if (clickDebounce()) {
            val json = Gson().toJson(this@SearchActivity)
            intent.putExtra(R.string.track_intent_key.toString(), json)
            startActivity(intent)
        }
    }
    override fun onFavoriteToggleClick(track: Track) {
        // 1
        viewModel.toggleFavorite(track)
    }

    lateinit var trackList: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderButton: Button
    private lateinit var historyTitle: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var simpleTextWatcher: TextWatcher
    private lateinit var searchHistoryStorage: SearchHistoryStorage


    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var viewModel: TrackSearchViewModel

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val inputEditText = findViewById<EditText>(R.id.et_find)
        val strValET = inputEditText.text.toString()
        outState.putString(ET_VALUE, strValET)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProvider(this, TrackSearchViewModel.getViewModelFactory())[TrackSearchViewModel::class.java]

        val fileName = getString(R.string.app_preference_file_name)
        val recentTracksListKey = getString(R.string.recent_tracks_list_key)
        val sharedPrefs = getSharedPreferences(fileName, MODE_PRIVATE)
        val btnBack = findViewById<ImageView>(R.id.find_activity_arrow_back)
        inputEditText = findViewById(R.id.et_find)
        val clearButton = findViewById<Button>(R.id.btn_clear)
        historyTitle = findViewById(R.id.history_tracks_title)
        placeholderImage = findViewById(R.id.statusImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        trackList = findViewById(R.id.recyclerView)
        placeholderButton = findViewById(R.id.btnUpdate)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        trackList.adapter=adapter

        searchHistoryStorage = SearchHistoryStorage(sharedPrefs, recentTracksListKey)

        // Скрываем кнопки
        clearButton.visibility = View.GONE
        placeholderButton.visibility = View.GONE

        searchHistoryStorage.loadFromFile()
      //adapter.trackList = tracks
        adapter.addObserver(searchHistoryStorage)
        historyAdapter.trackList = searchHistoryStorage.recentTracksList
        historyAdapter.addObserver(searchHistoryStorage)

        // Показываем историю
        if (searchHistoryStorage.recentTracksList.size > 0) {
            trackList.adapter=historyAdapter
            adapter.notifyDataSetChanged()
            showHistory(true)
        }

        // Обрабатываем нажатие на кнопку очистки поля ввода
        clearButton.setOnClickListener {
            inputEditText.text.clear()
            trackList.visibility = View.INVISIBLE
            placeholderMessage.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            placeholderButton.visibility = View.GONE
            hideKeyboard()
            if (searchHistoryStorage.recentTracksList.size > 0) {
                trackList.adapter=historyAdapter
                adapter.notifyDataSetChanged()
                showHistory(true)
            }
        }
        // Обрабатываем нажатие на кнопку обновить
        placeholderButton.setOnClickListener {
            if (placeholderButton.text == getString(R.string.btn_update)) {
                trackList.adapter=adapter
                viewModel?.searchQuery(inputEditText.text.toString())
            }
            if (placeholderButton.text == getString(R.string.btn_clear_history)) clearSearchingHistory()
        }
        // Инициализация TextWatcher
        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.INVISIBLE
                } else {
                    clearButton.visibility = View.VISIBLE
                    trackList.adapter=adapter
                    viewModel?.searchDebounce(changedText = s?.toString() ?: "")
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }
        //Добавляем созданный simpleTextWatcher к EditText
          simpleTextWatcher?.let{inputEditText.addTextChangedListener(it)}

        //Прописываем подписку на LiveData
        viewModel.observeState().observe(this) {
            render(it)
        }

        //Обрабатываем нажатие на кнопку "Назад"
        btnBack.setOnClickListener {
            onBackPressed()
        }

        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //-----ОБРАБОТКА ПОИСКОВОГО ЗАПРОСА---------------------------
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel?.searchQuery(inputEditText.text.toString())
            }
            false
        }

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val inputEditText = findViewById<EditText>(R.id.et_find)
        val strValET = savedInstanceState.getString(ET_VALUE)
        inputEditText.setText(strValET)
    }


    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher?.let{inputEditText.addTextChangedListener(it)}
    }

    fun render(state: SearchState) {
        when (state){
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Error -> showError(state.imageNum,state.messageNum,state.btnStatus)
            is SearchState.History -> showHistory(state.isVisible)
        }
    }

    fun showLoading() {
        progressBar.visibility=View.VISIBLE
        historyTitle.visibility=View.GONE
        placeholderButton.visibility=View.GONE
        placeholderImage.visibility=View.GONE
        placeholderMessage.visibility=View.GONE
        hideKeyboard()
    }

    fun showContent(tracks: ArrayList<Track>) {
        trackList.visibility = View.VISIBLE
        placeholderMessage.visibility = View.GONE
        progressBar.visibility = View.GONE
        adapter.trackList.clear()
        adapter.trackList.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    fun showError(imageNum: Int, messageNum: Int, btnStatus: Boolean) {
        progressBar.visibility=View.GONE
        placeholderImage.setImageResource(imageNum)
        placeholderImage.visibility=View.VISIBLE
        placeholderImage.setImageResource(imageNum)
        placeholderMessage.setText(messageNum)
        placeholderMessage.visibility=View.VISIBLE
        trackList.visibility=View.GONE
        placeholderButton.visibility = if (btnStatus) View.VISIBLE else View.GONE
        placeholderButton.setText(R.string.btn_update)
    }

    fun showHistory(isVisible: Boolean) {
        if(isVisible){
            historyTitle.visibility=View.VISIBLE
            placeholderButton.visibility = View.VISIBLE
            placeholderButton.setText(R.string.btn_clear_history)
            trackList.visibility=View.VISIBLE
        }else {
            historyTitle.visibility=View.GONE
            placeholderButton.visibility = View.GONE
            trackList.visibility=View.GONE
        }
    }

    fun clearSearchingHistory() {
        showHistory(false)
        searchHistoryStorage.clearStorage()
    }

    // Функция скрытия клавиатуры
    fun hideKeyboard() {
        currentFocus?.let { view ->
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

}


