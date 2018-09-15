package scenery.app.ui.login

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _isSpotifyInstalled = MutableLiveData<Boolean>()
    val isSpotifyInstalled: LiveData<Boolean> = _isSpotifyInstalled

    /**
     * Checks if the Spotify App is installed on the device
     */
    fun checkIfSpotifyInstalled(context: Context) {
        try {
            context.packageManager.getPackageInfo("com.spotify.music", 0)
            _isSpotifyInstalled.value = true
        } catch (e: PackageManager.NameNotFoundException) {
            _isSpotifyInstalled.value = false
        }
    }

}
