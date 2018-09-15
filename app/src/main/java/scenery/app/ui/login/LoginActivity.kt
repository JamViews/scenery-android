package scenery.app.ui.login

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.android.synthetic.main.activity_login.*
import scenery.app.R
import scenery.app.ui.MainActivity
import scenery.app.utils.dp

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProviders.of(this)[LoginViewModel::class.java]

        viewModel.isSpotifyInstalled.observe(this, Observer { value ->
            if (value == true) {
                buttonDescription.setText(R.string.spotify_auth_text)
                actionButton.setText(R.string.spotify_sign_in)

                actionButton.setOnClickListener {
                    val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN,
                            "sceneryapp://callback")

                    builder.setScopes(arrayOf("streaming"))
                    val request = builder.build()

                    AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)
                }
            } else {
                buttonDescription.setText(R.string.spotify_required)
                actionButton.setText(R.string.install_spotify)

                actionButton.setOnClickListener {
                    AuthenticationClient.openDownloadSpotifyActivity(this)
                }
            }
        })

        viewModel.checkIfSpotifyInstalled(this)
    }

    override fun onResume() {
        super.onResume()

        viewModel.checkIfSpotifyInstalled(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, data)
            if (response.type == AuthenticationResponse.Type.TOKEN) {
                Toast.makeText(this, "Signed in successfully", Toast.LENGTH_SHORT).show()

                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putString("userAccessToken", response.accessToken).apply()

                // TODO: Postpone this until Camera permissions are granted.
                // Restart the main activity since it was killed immediately after this activity was launched.
                startActivity(Intent(this, MainActivity::class.java))

                finish()
            } else {
                Toast.makeText(this, "Signed in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val CLIENT_ID = "9b86b4fe13c545c894a5d08db4a86a2f"
        private const val REQUEST_CODE = 1337
    }

}
