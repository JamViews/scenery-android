package scenery.app

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.otaliastudios.cameraview.*
import kotlinx.android.synthetic.main.activity_main.*
import scenery.app.utils.runOnWorkerThread

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]

        setSupportActionBar(toolbar)

        // Handle display cutouts/notches when necessary.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            toolbar.setOnApplyWindowInsetsListener { _, windowInsets ->
                val constraintSet = ConstraintSet()
                constraintSet.clone(base)

                constraintSet.connect(R.id.toolbar, ConstraintSet.TOP, R.id.base, ConstraintSet.TOP, windowInsets.systemWindowInsetTop)
                constraintSet.applyTo(base)

                windowInsets.consumeDisplayCutout()
            }
        }

        camera.setLifecycleOwner(this)

        // Activate tap-to-focus.
        camera.mapGesture(Gesture.TAP, GestureAction.FOCUS)
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                super.onPictureTaken(jpeg)

                viewModel.updatePhotoData(jpeg)
            }
        })

        button.setOnClickListener {
            camera.capturePicture()
        }

        switchFacing.setOnClickListener {
            viewModel.toggleCameraFacing()
        }

        viewModel.photoData.observe(this, Observer {
            if (it != null) {
                takenImage.setImageBitmap(it)
                takenImage.visibility = View.VISIBLE
                button.visibility = View.GONE
                switchFacing.visibility = View.GONE
            } else {
                takenImage.setImageBitmap(null)
                takenImage.visibility = View.GONE
                button.visibility = View.VISIBLE
                switchFacing.visibility = View.VISIBLE
            }
        })

        viewModel.cameraFacing.observe(this, Observer {
            it ?: return@Observer

            camera.facing = it
            when (it) {
                Facing.BACK -> switchFacing.setImageResource(R.drawable.ic_camera_front_black_24dp)
                Facing.FRONT -> switchFacing.setImageResource(R.drawable.ic_camera_rear_black_24dp)
            }
        })
    }

    override fun onBackPressed() {
        if (viewModel.photoData.value != null) {
            viewModel.updatePhotoData(null)
        } else {
            super.onBackPressed()
        }
    }
}
