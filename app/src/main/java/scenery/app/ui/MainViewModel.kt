package scenery.app.ui

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.otaliastudios.cameraview.CameraUtils
import com.otaliastudios.cameraview.Facing

class MainViewModel : ViewModel() {

    private val _photoData = MutableLiveData<Bitmap?>()
    val photoData: LiveData<Bitmap?> = _photoData

    // Stores a copy of the raw byte data for the image that is captured.
    private var rawPhotoData: ByteArray? = null

    private val _cameraFacing = MutableLiveData<Facing>()
    val cameraFacing: LiveData<Facing> = _cameraFacing

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

    fun getSongs() {
        // TODO: Access API services

        // Encode our captured image as a Base64 string to send to the API
        val encodedImage = Base64.encodeToString(rawPhotoData, Base64.DEFAULT)
    }

}