package scenery.app.ui

import android.content.Context
import android.graphics.Bitmap
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.otaliastudios.cameraview.CameraUtils
import com.otaliastudios.cameraview.Facing
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import scenery.app.data.ApiServiceProvider
import scenery.app.data.Request
import scenery.app.data.Response
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    private val disposables = CompositeDisposable()

    private val apiService = ApiServiceProvider.provideService()

    private val _photoData = MutableLiveData<Bitmap?>()
    val photoData: LiveData<Bitmap?> = _photoData

    // Stores a copy of the raw byte data for the image that is captured.
    private var rawPhotoData: ByteArray? = null

    private val _cameraFacing = MutableLiveData<Facing>()
    val cameraFacing: LiveData<Facing> = _cameraFacing

    private val _generatedPlaylistData = MutableLiveData<Response>()
    val generatedPlaylistData: LiveData<Response> = _generatedPlaylistData

    lateinit var currentSwatch: Palette.Swatch

    init {
        _cameraFacing.value = Facing.BACK
    }

    fun updatePhotoData(jpeg: ByteArray?) {
        if (jpeg != null) {
            CameraUtils.decodeBitmap(jpeg) {
                _photoData.postValue(it)
            }
        } else {
            _photoData.value = null
        }

        rawPhotoData = jpeg
    }

    /**
     * Toggles the direction the camera is facing.
     */
    fun toggleCameraFacing() {
        if (_cameraFacing.value == Facing.BACK) {
            _cameraFacing.value = Facing.FRONT
        } else if (_cameraFacing.value == Facing.FRONT) {
            _cameraFacing.value = Facing.BACK
        }
    }

    fun getSongs(context: Context) {
        CameraUtils.decodeBitmap(rawPhotoData) { bitmap ->
            val scaled = Bitmap.createScaledBitmap(bitmap, bitmap.width / 3, bitmap.height / 3, false)

            val stream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            val byteArray = stream.toByteArray()

            val token = PreferenceManager.getDefaultSharedPreferences(context).getString("userAccessToken", "")

            // Encode our captured image as a Base64 string to send to the API
            val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

            val request = Request(encodedImage, token = token!!)

            apiService.getPlaylists(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        _generatedPlaylistData.value = it
                    },{
                        it.printStackTrace()
                    })
                    .addTo(disposables)
        }
    }

}