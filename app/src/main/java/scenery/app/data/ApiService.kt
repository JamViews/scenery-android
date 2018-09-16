package scenery.app.data

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import scenery.app.data.spotify.SpotifyBody

interface ApiService {

    @GET("/search")
    fun getPlaylists(@Query("q") query: String, @Query("type") type: String = "playlist"): Single<SpotifyBody>

}