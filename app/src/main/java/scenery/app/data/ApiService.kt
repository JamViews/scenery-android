package scenery.app.data

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST
import scenery.app.data.spotify.SpotifyBody

interface ApiService {

    @POST("/spotify")
    fun getPlaylists(@Body body: Request): Single<Response>

}