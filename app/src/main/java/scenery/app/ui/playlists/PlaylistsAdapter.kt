package scenery.app.ui.playlists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.android.synthetic.main.playlist_item.view.*
import scenery.app.R
import scenery.app.data.spotify.Item
import scenery.app.data.spotify.SpotifyBody

class PlaylistsAdapter(private val data: SpotifyBody,
                       private val context: Context,
                       private val swatch: Palette.Swatch,
                       private val spotifyAppRemote: SpotifyAppRemote) : RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false))

    override fun getItemCount(): Int = data.playlists.items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data.playlists.items[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                spotifyAppRemote.playerApi.play(data.playlists.items[adapterPosition].uri)
            }
        }

        fun bind(item: Item) {
            itemView.name.text = item.name
            itemView.author.text = item.owner.displayName

            itemView.name.setTextColor(swatch.titleTextColor)
            itemView.author.setTextColor(swatch.titleTextColor)

            Glide.with(itemView).load(item.images[0].url).into(itemView.playlistCover)
        }

    }

}