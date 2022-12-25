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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CustomRecyclerAdapter(var trackList: ArrayList<Track>) : RecyclerView
.Adapter<CustomRecyclerAdapter.MyViewHolder>() {



    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var trackTextView: TextView = itemView.findViewById(R.id.trackName)
        var artistTextView: TextView = itemView.findViewById(R.id.trackArtist)
        var image:ImageView = itemView.findViewById(R.id.artistCover)

        fun bind(track: Track) {
            trackTextView.text = track.trackName
            val durationTrack = formatTrackDuration(track.trackTimeMillis)
            val artistNamePlusDuration = track.artistName.plus(" · ").plus(durationTrack)
            this.artistTextView.text = artistNamePlusDuration

            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.arrowback)
                .fitCenter()
                .transform(RoundedCorners(10))
                .into(image)
        }
        private fun formatTrackDuration(timeTrack: String?): String {
            if (timeTrack != null) {
                return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeTrack.toInt())
            }

            return itemView.context.getString(R.string.no_track_time)
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