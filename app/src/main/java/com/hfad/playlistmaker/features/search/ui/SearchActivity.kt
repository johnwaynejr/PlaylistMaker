package com.hfad.playlistmaker.features.search.ui

import android.content.Context
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
import com.hfad.playlistmaker.features.player.ui.PlayerActivity
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.features.player.domain.models.Track
import com.hfad.playlistmaker.features.search.domain.CustomRecyclerAdapter
import com.hfad.playlistmaker.features.search.data.SearchHistoryStorage
import com.hfad.playlistmaker.features.player.data.network.TrackResponse
import com.hfad.playlistmaker.features.search.iTunesApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    companion object {
        private const val ET_VALUE = "ET_VALUE"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesApi::class.java)

    private lateinit var updButton: Button
    private lateinit var inputEditText:EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var trackList: RecyclerView
    private lateinit var imageQueryStatus: ImageView
    private lateinit var searchHistoryStorage: SearchHistoryStorage
    private lateinit var recentTitle: TextView
   private  lateinit var progressBar: ProgressBar

    private val tracks = ArrayList<Track>()
    private var adapter = CustomRecyclerAdapter{
        if(clickDebounce()) {
            val intent = Intent(this, PlayerActivity::class.java)
            val json = Gson().toJson(it)
            searchHistoryStorage.saveToFile()
            intent.putExtra("track", json)
            startActivity(intent)
        }
    }
  private var  historyAdapter = CustomRecyclerAdapter{
        if(clickDebounce()) {
            val intent = Intent(this, PlayerActivity::class.java)
            val json = Gson().toJson(it)
            intent.putExtra("track", json)
            startActivity(intent)
        }
    }

    //Инициализация переменных для работы с потоками
    private val searchRunnable = Runnable { searchQuery() }
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val inputEditText = findViewById<EditText>(R.id.et_find)
        val strValET=inputEditText.text.toString()
        outState.putString(ET_VALUE,strValET)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val inputEditText = findViewById<EditText>(R.id.et_find)
        val strValET=savedInstanceState.getString(ET_VALUE)
        inputEditText.setText(strValET)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        val btnBack = findViewById<ImageView>(R.id.find_activity_arrow_back)
        inputEditText = findViewById<EditText>(R.id.et_find)
        val clearButton = findViewById<Button>(R.id.btn_clear)
        val fileName = getString(R.string.app_preference_file_name)
        val recentTracksListKey = getString(R.string.recent_tracks_list_key)
        val sharedPrefs = getSharedPreferences(fileName, MODE_PRIVATE)
        recentTitle = findViewById(R.id.recent_tracks_title)
        imageQueryStatus = findViewById(R.id.statusImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        trackList = findViewById(R.id.recyclerView)
        updButton =findViewById(R.id.btnUpdate)
        progressBar=findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        searchHistoryStorage = SearchHistoryStorage(sharedPrefs, recentTracksListKey)
        searchHistoryStorage.loadFromFile()

        adapter.trackList = tracks
        adapter.addObserver(searchHistoryStorage)
        historyAdapter.trackList=searchHistoryStorage.recentTracksList
        historyAdapter.addObserver(searchHistoryStorage)


    // Скрываем кнопки
        clearButton.visibility = View.GONE
        updButton.visibility = View.GONE

    // Обрабатываем нажатие на кнопку очистки поля ввода
        clearButton.setOnClickListener {
            inputEditText.text.clear()
            trackList.visibility = View.INVISIBLE
            placeholderMessage.visibility=View.GONE
            imageQueryStatus.visibility=View.GONE
            updButton.visibility=View.GONE
            hideKeyboard()
            if (searchHistoryStorage.recentTracksList.size > 0) {
                showHistory()
            }
        }
        // Обрабатываем нажатие на кнопку обновить
        updButton.setOnClickListener {
            if (updButton.text == getString(R.string.btn_update)) searchQuery()
            if (updButton.text == getString(R.string.btn_clear_history)) clearSearchingHistory()
        }

        // Показываем историю
        if (searchHistoryStorage.recentTracksList.size > 0) {
            showHistory()
        }

    // Инициализация TextWatcher
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.INVISIBLE
                } else {
                    clearButton.visibility = View.VISIBLE
                    searchDebounce()
                }

            }
            override fun afterTextChanged(p0: Editable?) {
                //empty
            }
        }

    //Добавляем созданный simpleTextWatcher к EditText
        inputEditText.addTextChangedListener(simpleTextWatcher)

    //Обрабатываем нажатие на кнопку "Назад"
        btnBack.setOnClickListener {
            onBackPressed()
        }

        adapter.trackList=tracks
        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    //-----ОБРАБОТКА ПОИСКОВОГО ЗАПРОСА---------------------------
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchQuery()
            }
            false
        }
    }

    //Функция выполнения поискового запроса
    private fun searchQuery(){
    trackList.adapter = adapter
    recentTitle.visibility = View.GONE
    updButton.visibility = View.GONE
    trackList.visibility = View.VISIBLE
    imageQueryStatus.visibility = View.GONE
    placeholderMessage.visibility = View.GONE
    progressBar.visibility = View.VISIBLE
    hideKeyboard()
    if (inputEditText.text.isNotEmpty()) {
        iTunesService.search(inputEditText.text.toString()).enqueue(object :
            Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>) {
                progressBar.visibility = View.GONE // Прячем ProgressBar после успешного выполнения запроса
                if (response.code() == 200) {
                    tracks.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        trackList.adapter!!.notifyDataSetChanged()
                    }
                    if (tracks.isEmpty()) {
                       showQueryPlaceholder(R.drawable.findnothing, R.string.nothing_found,false)
                    }
                } else {

                    showQueryPlaceholder(R.drawable.finderror, R.string.something_went_wrong,true)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
               showQueryPlaceholder(R.drawable.nointernet, R.string.no_internet,true)
            }

        })
    }
    true

}
    private fun showHistory() {
        recentTitle.visibility = View.VISIBLE
        updButton.text = getString(R.string.btn_clear_history)
        updButton.visibility = View.VISIBLE

        trackList.adapter = historyAdapter
        trackList.adapter!!.notifyDataSetChanged()
        trackList.visibility = View.VISIBLE

    }

    private fun clearSearchingHistory() {
        recentTitle.visibility = View.GONE
        updButton.visibility = View.GONE
        trackList.visibility = View.INVISIBLE
        searchHistoryStorage.clearStorage()
    }
// Функция отображения заглушки при неудачном поиске, скрытие списка треков и отображения кнопки "Обновить"
    private fun showQueryPlaceholder(image: Int, message: Int,updBtnStatus: Boolean) {
        tracks.clear()
        imageQueryStatus.visibility = View.VISIBLE
        imageQueryStatus.setImageResource(image)
        placeholderMessage.visibility = View.VISIBLE
        placeholderMessage.setText(message)
        trackList.visibility = View.INVISIBLE
        updButton.text = getString(R.string.btn_update)
        if(updBtnStatus) updButton.visibility=View.VISIBLE else updButton.visibility=View.GONE
    }
// Функция скрытия клавиатуры
    private fun hideKeyboard() {
        this.currentFocus?.let { view ->
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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

