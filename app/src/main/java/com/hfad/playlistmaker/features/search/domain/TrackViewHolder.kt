package com.hfad.playlistmaker.features.search.domain

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.features.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale



class TrackViewHolder(itemView: View,private val clickListener: TrackRecyclerAdapter.TrackClickListener) : RecyclerView.ViewHolder(itemView) {
    var trackTextView: TextView = itemView.findViewById(R.id.trackName)
    var artistTextView: TextView = itemView.findViewById(R.id.trackArtist)
    var image: ImageView = itemView.findViewById(R.id.artistCover)
    var inFavoriteToggle: ImageView = itemView.findViewById(R.id.playerAddToFavoritesBtn)

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

        inFavoriteToggle.setImageDrawable(getFavoriteToggleDrawable(track.inFavorite))
        // 2
        itemView.setOnClickListener { clickListener.onTrackClick(track) }
        // 3
        inFavoriteToggle.setOnClickListener { clickListener.onFavoriteToggleClick(track) }
    }

    private fun getFavoriteToggleDrawable(inFavorite: Boolean): Drawable? {
        return itemView.context.getDrawable(
            if(inFavorite) R.drawable.active_favorite else R.drawable.inactive_favorite
        )
    }

    private fun formatTrackDuration(timeTrack: String?): String {
        if (timeTrack != null) {
            return SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeTrack.toInt())
        }

        return itemView.context.getString(R.string.no_track_time)
    }

}
