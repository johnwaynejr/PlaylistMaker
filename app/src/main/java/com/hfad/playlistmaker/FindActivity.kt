package com.hfad.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class FindActivity : AppCompatActivity() {

    companion object {
        const val ET_VALUE = "ET_VALUE"
    }

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesApi::class.java)

    private lateinit var updButton: Button
    private lateinit var queryInput: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var trackList: RecyclerView
    private lateinit var imageQueryStatus: ImageView
    private lateinit var searchHistory: SearchHistory
    private lateinit var recentTitle: TextView
    private lateinit var historyAdapter: CustomRecyclerAdapter

    private val tracks = ArrayList<Track>()
    private var adapter = CustomRecyclerAdapter(tracks)

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

    override fun onStop() {
        super.onStop()
        searchHistory.saveToFile()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        val btnBack = findViewById<ImageView>(R.id.find_activity_arrow_back)
        val inputEditText = findViewById<EditText>(R.id.et_find)
        val clearButton = findViewById<Button>(R.id.btn_clear)
        val fileName = getString(R.string.app_preference_file_name)
        val recentTracksListKey = getString(R.string.recent_tracks_list_key)
        val sharedPrefs = getSharedPreferences(fileName, MODE_PRIVATE)
        recentTitle = findViewById(R.id.recent_tracks_title)
        imageQueryStatus = findViewById(R.id.statusImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        queryInput = findViewById(R.id.et_find)
        trackList = findViewById(R.id.recyclerView)
        updButton =findViewById(R.id.btnUpdate)

        searchHistory = SearchHistory(sharedPrefs, recentTracksListKey)
        searchHistory.loadFromFile()

        adapter = CustomRecyclerAdapter(tracks)
        adapter.addObserver(searchHistory)
        historyAdapter = CustomRecyclerAdapter(searchHistory.recentTracksList)
        historyAdapter.addObserver(searchHistory)


    // Скрываем кнопки
        clearButton.visibility = View.GONE
        updButton.visibility = View.GONE

    // Обрабатываем нажатие на кнопку очистки поля ввода
        clearButton.setOnClickListener {
            inputEditText.text.clear()
            trackList.visibility = View.GONE
            placeholderMessage.visibility=View.GONE
            imageQueryStatus.visibility=View.GONE
            updButton.visibility=View.GONE
            hideKeyboard()
            showHistory()
        }
        // Обрабатываем нажатие на кнопку обновить
        updButton.setOnClickListener {
            if (updButton.text == getString(R.string.btn_update)) searchQuery()
            if (updButton.text == getString(R.string.btn_clear_history)) clearSearchingHistory()
        }

        // Показываем историю
        if (searchHistory.recentTracksList.size > 0) {
            showHistory()
        }

    // Инициализация TextWatcher
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =

                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.INVISIBLE
                } else {
                    clearButton.visibility = View.VISIBLE
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
        queryInput.setOnEditorActionListener { _, actionId, _ ->
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
    hideKeyboard()
    if (queryInput.text.isNotEmpty()) {
        iTunesService.search(queryInput.text.toString()).enqueue(object :
            Callback<SongResponse> {
            override fun onResponse(
                call: Call<SongResponse>,
                response: Response<SongResponse>
            ) {
                if (response.code() == 200) {
                    tracks.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        trackList.adapter!!.notifyDataSetChanged()
                    }
                    if (tracks.isEmpty()) {
                       showQueryPlaceholder(R.drawable.findnothing,R.string.nothing_found,false)
                    }
                } else {

                    showQueryPlaceholder(R.drawable.finderror,R.string.something_went_wrong,true)
                }
            }

            override fun onFailure(call: Call<SongResponse>, t: Throwable) {
               showQueryPlaceholder(R.drawable.nointernet,R.string.no_internet,true)
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
        trackList.visibility = View.GONE
        searchHistory.clearHistory()
    }
// Функция отображения заглушки при неудачном поиске, скрытие списка треков и отображения кнопки "Обновить"
    private fun showQueryPlaceholder(image: Int, message: Int,updBtnStatus: Boolean) {
        tracks.clear()
        imageQueryStatus.visibility = View.VISIBLE
        imageQueryStatus.setImageResource(image)
        placeholderMessage.visibility = View.VISIBLE
        placeholderMessage.setText(message)
        trackList.visibility = View.GONE
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
}


