package scenery.app.data.spotify

data class Playlists(val href: String,
                     val items: List<Item>,
                     val limit: Int,
                     val next: String?,
                     val offset: Int,
                     val previous: String?,
                     val total: Int)