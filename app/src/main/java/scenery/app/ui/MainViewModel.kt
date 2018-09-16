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
import org.tensorflow.contrib.android.TensorFlowInferenceInterface
import scenery.app.data.ApiServiceProvider
import scenery.app.data.Request
import scenery.app.data.Response
import java.io.ByteArrayOutputStream
import android.R.attr.bitmap
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import io.reactivex.rxkotlin.subscribeBy
import scenery.app.ImageClassifier
import kotlin.math.max


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
        val imageClassifier = ImageClassifier(context.assets)


        // TODO: 1. Process bitmap image to feed into TF
        // TODO: 2. Let TF process and classify image
        // TODO: 3. Fetch output node values into a priority queue to determine best match
        // TODO: 4. Perform Spotify search and display results. Ez.

        CameraUtils.decodeBitmap(rawPhotoData) { bitmap ->

            imageClassifier.recognizeImage(bitmap).subscribeBy {
                Log.d("MainViewModel", "Size: ${it.size}")
            }
        }

            /*val scaled = Bitmap.createScaledBitmap(bitmap, bitmap.width / 4, bitmap.height / 4, false)

            val inputSize = max(scaled.width, scaled.height)

            val imageMean = 0
            val imageStd = 255

            val intValues = IntArray(inputSize * inputSize)
            val floatValues = FloatArray(inputSize * inputSize * 3)

            // Preprocess the image data from 0-255 int to normalized float based
            // on the provided parameters.
            scaled.getPixels(intValues, 0, scaled.width, 0, 0, scaled.width, scaled.height)
            for (i in 0 until intValues.size) {
                val `val` = intValues[i]
                floatValues[i * 3 + 0] = (((`val` shr 16 and 0xFF) - imageMean) / imageStd).toFloat()
                floatValues[i * 3 + 1] = (((`val` shr 8 and 0xFF) - imageMean) / imageStd).toFloat()
                floatValues[i * 3 + 2] = (((`val` and 0xFF) - imageMean) / imageStd).toFloat()
            }

            val tensorInterface = TensorFlowInferenceInterface(context.assets, "graph.pb")

            tensorInterface.feed("input/BottleneckInputPlaceholder", floatValues, 1, inputSize.toLong(), inputSize.toLong(), 3)

            tensorInterface.run(arrayOf("final_result"))

            val output = FloatArray(8)
            tensorInterface.fetch("final_result", output)
        }*/
            /*val scaled = Bitmap.createScaledBitmap(bitmap, bitmap.width / 3, bitmap.height / 3, false)

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
        }*/




    }

}