package com.hfad.playlistmaker.features.search.presentation

import android.app.Activity
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.features.Creator
import com.hfad.playlistmaker.features.search.data.SearchHistoryStorage
import com.hfad.playlistmaker.features.search.domain.TrackRecyclerAdapter
import com.hfad.playlistmaker.features.search.domain.api.TrackInteractor
import com.hfad.playlistmaker.features.search.domain.models.Track

class TrackSearchController(private val activity: Activity,
                            private val adapter: TrackRecyclerAdapter,
                            private val historyAdapter: TrackRecyclerAdapter,
                            private val historyStorage: SearchHistoryStorage) {

    private val trackInteractor = Creator.provideTrackInteractor()

    companion object {
       private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private lateinit var updButton: Button
    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var trackList: RecyclerView
    private lateinit var imageQueryStatus: ImageView
    private lateinit var recentTitle: TextView
    private  lateinit var progressBar: ProgressBar

    private val tracks = ArrayList<Track>()

    //Инициализация переменных для работы с потоками
    private val searchRunnable = Runnable { searchQuery() }
    private val handler = Handler(Looper.getMainLooper())

    fun onCreate() {

        val btnBack = activity.findViewById<ImageView>(R.id.find_activity_arrow_back)
        inputEditText = activity.findViewById(R.id.et_find)
        val clearButton = activity.findViewById<Button>(R.id.btn_clear)
        recentTitle = activity.findViewById(R.id.recent_tracks_title)
        imageQueryStatus = activity.findViewById(R.id.statusImage)
        placeholderMessage = activity.findViewById(R.id.placeholderMessage)
        trackList = activity.findViewById(R.id.recyclerView)
        updButton =activity.findViewById(R.id.btnUpdate)
        progressBar=activity.findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        historyStorage.loadFromFile()

        adapter.trackList = tracks
        adapter.addObserver(historyStorage)
        historyAdapter.trackList=historyStorage.recentTracksList
        historyAdapter.addObserver(historyStorage)

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
            if (historyStorage.recentTracksList.size > 0) {
                showHistory()
            }
        }
        // Обрабатываем нажатие на кнопку обновить
        updButton.setOnClickListener {
            if (updButton.text == activity.getString(R.string.btn_update)) searchQuery()
            if (updButton.text == activity.getString(R.string.btn_clear_history)) clearSearchingHistory()
        }

        // Показываем историю
        if (historyStorage.recentTracksList.size > 0) {
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
            activity.onBackPressed()
        }

        adapter.trackList=tracks
        trackList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        //-----ОБРАБОТКА ПОИСКОВОГО ЗАПРОСА---------------------------
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchQuery()
            }
            false
        }
    }
    fun onDestroy() {
        handler.removeCallbacks(searchRunnable)
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
            trackInteractor.search(inputEditText.text.toString(), object : TrackInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>) {
                    handler.post {
                        progressBar.visibility = View.GONE
                        tracks.clear()
                        tracks.addAll(foundTracks)
                        trackList.visibility = View.VISIBLE
                        adapter.notifyDataSetChanged()
                        if (tracks.isEmpty()) {
                            showQueryPlaceholder(R.drawable.findnothing, R.string.nothing_found,false)
                        } else {
                            //showQueryPlaceholder(R.drawable.finderror, R.string.something_went_wrong,true)
                        }
                    }

                      // showQueryPlaceholder(R.drawable.nointernet, R.string.no_internet,true)
                }
            })
        }
    }

    private fun showHistory() {
        recentTitle.visibility = View.VISIBLE
        updButton.text = activity.getString(R.string.btn_clear_history)
        updButton.visibility = View.VISIBLE

        trackList.adapter = historyAdapter
        trackList.adapter!!.notifyDataSetChanged()
        trackList.visibility = View.VISIBLE

    }

    private fun clearSearchingHistory() {
        recentTitle.visibility = View.GONE
        updButton.visibility = View.GONE
        trackList.visibility = View.INVISIBLE
        historyStorage.clearStorage()
    }
    // Функция отображения заглушки при неудачном поиске, скрытие списка треков и отображения кнопки "Обновить"
    private fun showQueryPlaceholder(image: Int, message: Int,updBtnStatus: Boolean) {
        tracks.clear()
        imageQueryStatus.visibility = View.VISIBLE
        imageQueryStatus.setImageResource(image)
        placeholderMessage.visibility = View.VISIBLE
        placeholderMessage.setText(message)
        trackList.visibility = View.INVISIBLE
        updButton.text = activity.getString(R.string.btn_update)
        if(updBtnStatus) updButton.visibility= View.VISIBLE else updButton.visibility= View.GONE
    }
    // Функция скрытия клавиатуры
    private fun hideKeyboard() {
        activity.currentFocus?.let { view ->
            val inputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

}