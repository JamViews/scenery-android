package scenery.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.otaliastudios.cameraview.Gesture
import com.otaliastudios.cameraview.GestureAction
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        camera.setLifecycleOwner(this)

        // Activate tap-to-focus
        camera.mapGesture(Gesture.TAP, GestureAction.FOCUS)
    }
}
