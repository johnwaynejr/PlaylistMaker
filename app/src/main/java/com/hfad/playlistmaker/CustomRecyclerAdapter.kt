package com.hfad.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class CustomRecyclerAdapter(private val trackList: ArrayList<Track>) : RecyclerView
.Adapter<CustomRecyclerAdapter.MyViewHolder>() {



    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var trackTextView: TextView = itemView.findViewById(R.id.trackName)
        var artistTextView: TextView = itemView.findViewById(R.id.trackArtist)
        var image:ImageView = itemView.findViewById(R.id.artistCover)

        fun bind(track: Track) {
            trackTextView.text = track.trackName
            this.artistTextView.text = "${track.artistName} · ${track.trackTime}"

            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.arrowback)
                .fitCenter()
                .transform(RoundedCorners(10))
                .into(image)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.track_list_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount() = trackList.size
}