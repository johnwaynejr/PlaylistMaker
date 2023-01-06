package com.hfad.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class CustomRecyclerAdapter(
    var trackList: ArrayList<Track>
    ) : RecyclerView.Adapter<MyViewHolder>(), Observable  {

    private lateinit var searchHistory: Observer


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_list_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Трек добавлен в историю",
                Toast.LENGTH_SHORT
            ).show()

            searchHistory.addTrackToRecentList(trackList[position])
        }
    }

    override fun getItemCount() = trackList.size

    override fun addObserver(observer: Observer) {
        searchHistory = observer
    }
}