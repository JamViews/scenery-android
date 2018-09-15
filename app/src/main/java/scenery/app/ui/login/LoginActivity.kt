package scenery.app.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.spotify.sdk.android.authentication.AuthenticationClient
import kotlinx.android.synthetic.main.activity_login.*
import scenery.app.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        installSpotifyButton.setOnClickListener {
            AuthenticationClient.openDownloadSpotifyActivity(this)
        }
    }
}
