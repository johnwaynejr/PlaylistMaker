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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.util.Creator
import com.hfad.playlistmaker.features.player.ui.PlayerActivity
import com.hfad.playlistmaker.features.search.data.SearchHistoryStorage
import com.hfad.playlistmaker.features.search.domain.TrackRecyclerAdapter
import com.hfad.playlistmaker.features.search.domain.models.Track
import com.hfad.playlistmaker.features.search.presentation.SearchView
import com.hfad.playlistmaker.features.search.presentation.TrackSearchPresenter


class SearchActivity : AppCompatActivity(), SearchView {

    companion object {
        private const val ET_VALUE = "ET_VALUE"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
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
    private lateinit var trackSearchPresenter: TrackSearchPresenter

    private var adapter = TrackRecyclerAdapter {
        if (clickDebounce()) {
            searchHistoryStorage.addTrackToStorage(it)
            searchHistoryStorage.saveToFile()
            val intent = Intent(this, PlayerActivity::class.java)
            val json = Gson().toJson(it)
            intent.putExtra(R.string.track_intent_key.toString(), json)
            startActivity(intent)
        }
    }

    private var historyAdapter = TrackRecyclerAdapter {
        if (clickDebounce()) {
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
        val strValET = inputEditText.text.toString()
        outState.putString(ET_VALUE, strValET)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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

        searchHistoryStorage = SearchHistoryStorage(sharedPrefs, recentTracksListKey)

        trackSearchPresenter = Creator.provideTrackSearchPresenter(
            this,
            this,
            historyAdapter,
            searchHistoryStorage
        )

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
            trackSearchPresenter.showHistory()
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
                trackSearchPresenter.showHistory()
            }
        }
        // Обрабатываем нажатие на кнопку обновить
        placeholderButton.setOnClickListener {
            if (placeholderButton.text == getString(R.string.btn_update)) trackSearchPresenter.searchQuery(inputEditText.text.toString())
            if (placeholderButton.text == getString(R.string.btn_clear_history)) trackSearchPresenter.clearSearchingHistory()
        }
        // Инициализация TextWatcher
        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.INVISIBLE
                } else {
                    clearButton.visibility = View.VISIBLE
                    trackSearchPresenter.searchDebounce(changedText = s?.toString() ?: "")
                }

            }

            override fun afterTextChanged(p0: Editable?) {
                //empty
            }
        }

        //Добавляем созданный simpleTextWatcher к EditText
          simpleTextWatcher?.let{inputEditText.addTextChangedListener(it)}

        //Обрабатываем нажатие на кнопку "Назад"
        btnBack.setOnClickListener {
            onBackPressed()
        }

        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        //-----ОБРАБОТКА ПОИСКОВОГО ЗАПРОСА---------------------------
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                trackSearchPresenter.searchQuery(inputEditText.text.toString())
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
        trackSearchPresenter.onDestroy()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun showProgressBar(isVisible:Boolean) {
       progressBar.visibility=if(isVisible) View.VISIBLE else View.GONE
    }

    override fun showTrackList(isVisible: Boolean) {
        trackList.visibility=if(isVisible) View.VISIBLE else View.GONE
    }

    override fun showPlaceholderMessage(isVisible: Boolean) {
        placeholderMessage.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setPlaceholderMessage(messageNum: Int) {
        placeholderMessage.setText(messageNum)
    }

    override fun showPlaceholderImage(isVisible: Boolean) {
        placeholderImage.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setPlaceholderImage(imageNum: Int) {
       placeholderImage.setImageResource(imageNum)
    }

    override fun showPlaceholderButton(isVisible: Boolean) {
        placeholderButton.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun setTextPlaceholderButton(textNum: Int) {
        placeholderButton.setText(textNum)
    }

    override fun showHistoryTitle(isVisible: Boolean) {
        historyTitle.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun initAdapter() {
        trackList.adapter=adapter
    }

    override fun initAdapter(adapter: TrackRecyclerAdapter) {
        trackList.adapter=adapter
    }
    // Функция скрытия клавиатуры
    override fun hideKeyboard() {
        currentFocus?.let { view ->
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun updateTrackList(newTrackList: List<Track>) {
        adapter.trackList.clear()
        adapter.trackList.addAll(newTrackList)
    }

    override fun notifyAdapter() {
        adapter.notifyDataSetChanged()
    }
}


