package scenery.app.data

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceProvider {

    @JvmStatic
    fun provideService(): ApiService {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://jam-views.azurewebsites.net/")
                .build()
                .create(ApiService::class.java)
    }

}