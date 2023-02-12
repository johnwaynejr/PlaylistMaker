package com.hfad.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class CustomRecyclerAdapter(val clickListener: TrackClickListener) : RecyclerView.Adapter<MyViewHolder>(), Observable  {

    var trackList=ArrayList<Track>()
    private lateinit var searchHistory: Observer

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_list_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            searchHistory.addTrackToRecentList(trackList[position])
            searchHistory.saveToFile()
            holder.itemView.setOnClickListener { clickListener.onTrackClick(trackList.get(position)) }
            //val intent = Intent(it.context, PlayerActivity::class.java)
            //holder.itemView.context.startActivity(intent)

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