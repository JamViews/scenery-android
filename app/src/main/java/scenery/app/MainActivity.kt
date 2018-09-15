package scenery.app

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintSet
import com.otaliastudios.cameraview.Gesture
import com.otaliastudios.cameraview.GestureAction
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        camera.mapGesture(Gesture.LONG_TAP, GestureAction.CAPTURE)
    }
}
