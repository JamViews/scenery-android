package scenery.app.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.Facing
import com.otaliastudios.cameraview.Gesture
import com.otaliastudios.cameraview.GestureAction
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.android.synthetic.main.activity_main.*
import scenery.app.R
import scenery.app.ui.login.LoginActivity
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import scenery.app.ui.login.LoginActivity.Companion.CLIENT_ID
import scenery.app.ui.playlists.PlaylistsAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    private val bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout> by lazy { BottomSheetBehavior.from<ConstraintLayout>(bottomSheet) }

    private lateinit var spotifyAppRemote: SpotifyAppRemote

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]

        if (PreferenceManager.getDefaultSharedPreferences(this).getString("userAccessToken", null) == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()

            return
        }

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        // Set the Spotify connection parameters
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri("sceneryapp://callback")
                .showAuthView(true)
                .build()
        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onFailure(throwable: Throwable?) {
                throwable?.printStackTrace()
            }

            override fun onConnected(remote: SpotifyAppRemote?) {
                spotifyAppRemote = remote!!
            }
        })

        // Hide the bottom sheet by default
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

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
            camera.captureSnapshot()
        }

        switchFacing.setOnClickListener {
            viewModel.toggleCameraFacing()
        }

        signInButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        playButton.setOnClickListener {
            if (::spotifyAppRemote.isInitialized) {
                spotifyAppRemote.playerApi.play("spotify:user:spotify:playlist:37i9dQZF1DXdPec7aLTmlC")
            }
        }

        viewModel.photoData.observe(this, Observer { bitmap ->
            if (bitmap != null) {
                takenImage.setImageBitmap(bitmap)
                takenImage.visibility = View.VISIBLE
                button.visibility = View.GONE
                switchFacing.visibility = View.GONE

                Palette.from(bitmap).generate {
                    val swatch = it?.lightVibrantSwatch ?: it?.darkVibrantSwatch ?: it?.lightMutedSwatch ?: it?.darkMutedSwatch ?: it?.dominantSwatch ?: return@generate

                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheet.setBackgroundColor(swatch.rgb)



                    moodTitle.setTextColor(swatch.titleTextColor)

                    playButton.iconTint = ColorStateList.valueOf(swatch.titleTextColor)
                    playButton.rippleColor = ColorStateList.valueOf(swatch.titleTextColor)

                    viewModel.currentSwatch = swatch
                }

                viewModel.getSongs(this)

            } else {
                takenImage.setImageBitmap(null)
                takenImage.visibility = View.GONE
                button.visibility = View.VISIBLE
                switchFacing.visibility = View.VISIBLE

                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })

        viewModel.generatedPlaylistData.observe(this, Observer {
            moodTitle.text = it.key
            recyclerView.adapter = PlaylistsAdapter(it.body, this, viewModel.currentSwatch, spotifyAppRemote)
        })

        viewModel.cameraFacing.observe(this, Observer {
            it ?: return@Observer

            camera.facing = it
            when (it) {
                Facing.BACK -> switchFacing.setIconResource(R.drawable.ic_camera_front_black_24dp)
                Facing.FRONT -> switchFacing.setIconResource(R.drawable.ic_camera_rear_black_24dp)
            }
        })
    }

    override fun onStop() {
        super.onStop()

        SpotifyAppRemote.CONNECTOR.disconnect(spotifyAppRemote)
    }

    override fun onBackPressed() {
        if (viewModel.photoData.value != null) {
            viewModel.updatePhotoData(null)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
