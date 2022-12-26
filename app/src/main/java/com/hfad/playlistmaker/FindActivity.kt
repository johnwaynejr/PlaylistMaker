package com.hfad.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
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

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private lateinit var updButton: Button
    private lateinit var queryInput: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var trackList: RecyclerView
    private lateinit var imageQueryStatus: ImageView


    private val adapter = CustomRecyclerAdapter(tracks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        val btnBack = findViewById<ImageView>(R.id.find_activity_arrow_back)
        val inputEditText = findViewById<EditText>(R.id.et_find)
        val clearButton = findViewById<Button>(R.id.btn_clear)
        imageQueryStatus = findViewById(R.id.statusImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        queryInput = findViewById(R.id.et_find)
        trackList = findViewById(R.id.recyclerView)
        updButton =findViewById(R.id.btnUpdate)

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

    //-----ОБРАБОТКА ПОИСКОВОГО ЗАПРОСА--------------------------------------------------
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                trackList.adapter = adapter
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
                                    trackList.visibility = View.GONE
                                    showQueryPlaceholder(R.drawable.findnothing,R.string.nothing_found)
                                } else {
                                    //showMessage("", "")
                                }
                            } else {
                                trackList.visibility = View.GONE
                                updButton.visibility=View.VISIBLE
                                showQueryPlaceholder(R.drawable.finderror,R.string.something_went_wrong)
                            }
                        }

                        override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                            trackList.visibility = View.GONE
                            updButton.visibility=View.VISIBLE
                            showQueryPlaceholder(R.drawable.nointernet,R.string.no_internet)
                        }

                    })
                }
                true

            }
            false
        }
    }
// Функция отображения заглушки при неудачном поиске
    private fun showQueryPlaceholder(image: Int, message: Int) {
        tracks.clear()
        imageQueryStatus.visibility = View.VISIBLE
        imageQueryStatus.setImageResource(image)
        placeholderMessage.visibility = View.VISIBLE
        placeholderMessage.setText(message)
    }
// Функция скрытия клавиатуры
    private fun hideKeyboard() {
        this.currentFocus?.let { view ->
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /*private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }*/

}


