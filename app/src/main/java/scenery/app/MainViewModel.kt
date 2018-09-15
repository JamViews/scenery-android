package scenery.app

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otaliastudios.cameraview.CameraUtils

class MainViewModel : ViewModel() {

    private val _photoData = MutableLiveData<Bitmap?>()
    val photoData: LiveData<Bitmap?> = _photoData

    // Stores a copy of the raw byte data for the image that is captured.
    private var rawPhotoData: ByteArray? = null

    fun updatePhotoData(jpeg: ByteArray?) {
        CameraUtils.decodeBitmap(jpeg) {
            _photoData.postValue(it)
            rawPhotoData = jpeg
        }
    }

    fun getSongs() {
        // TODO: Access API services

        // Encode our captured image as a Base64 string to send to the API
        val encodedImage = Base64.encodeToString(rawPhotoData, Base64.DEFAULT)
    }

}