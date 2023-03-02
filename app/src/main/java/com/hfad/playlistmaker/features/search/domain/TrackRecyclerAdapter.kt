package com.hfad.playlistmaker.features.search.domain

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.features.search.domain.models.Track
import com.hfad.playlistmaker.features.search.domain.api.Observable
import com.hfad.playlistmaker.features.search.domain.api.Observer


class TrackRecyclerAdapter(val clickListener: TrackClickListener) :
    RecyclerView.Adapter<TrackViewHolder>(),
    Observable {

    var trackList = ArrayList<Track>()
    private lateinit var searchHistory: Observer

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_list_layout, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
           clickListener.onTrackClick(trackList.get(position))
        }
    }

    override fun getItemCount() = trackList.size

    override fun addObserver(observer: Observer) {
        searchHistory = observer
    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

}