package com.hfad.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
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
    //-----------------------------------------
    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesApi::class.java)

    private val tracks = ArrayList<Track>()
    private lateinit var searchButton: Button
    private lateinit var queryInput: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var trackList: RecyclerView
    private lateinit var imageQueryStatus: ImageView


    private val adapter = CustomRecyclerAdapter(tracks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find)

        val inputEditText = findViewById<EditText>(R.id.et_find)
        val clearButton = findViewById<Button>(R.id.btn_clear)
        imageQueryStatus = findViewById<ImageView>(R.id.statusImage)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        searchButton = findViewById(R.id.searchBtn)
        queryInput = findViewById(R.id.et_find)
        trackList = findViewById(R.id.recyclerView)
        clearButton.visibility = View.INVISIBLE

        clearButton.setOnClickListener {
            inputEditText.text.clear()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(clearButton.windowToken, 0)
        }
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

        //добавляем созданный simpleTextWatcher к EditText
        inputEditText.addTextChangedListener(simpleTextWatcher)

        //Обработка нажатия на кнопку "Назад"
        val btnBack = findViewById<ImageView>(R.id.find_activity_arrow_back)
        btnBack.setOnClickListener {
            onBackPressed()
        }

        adapter.trackList=tracks
        // RECYCLE VIEW -------------------------------------------------------------------------
        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        //recyclerView.adapter = CustomRecyclerAdapter(tracks)

        searchButton.setOnClickListener {
            trackList.adapter = adapter
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(searchButton.windowToken, 0)
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
                                showQueryPlaceholder(R.drawable.findnothing,R.string.nothing_found)
                            } else {
                                //showMessage("", "")
                            }
                        } else {
                               showQueryPlaceholder(R.drawable.finderror,R.string.something_went_wrong)
                        }
                    }

                    override fun onFailure(call: Call<SongResponse>, t: Throwable) {
                        showQueryPlaceholder(R.drawable.nointernet,R.string.no_internet)
                    }

                })
            }
        }
    }

    private fun showQueryPlaceholder(image: Int, message: Int) {
        tracks.clear()
        imageQueryStatus.visibility = View.VISIBLE
        imageQueryStatus.setImageResource(image)
        placeholderMessage.visibility = View.VISIBLE
        placeholderMessage.setText(message)
    }

    private fun showMessage(text: String, additionalMessage: String) {
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
    }

}


