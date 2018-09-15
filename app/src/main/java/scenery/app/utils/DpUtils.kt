package scenery.app.utils

import android.content.res.Resources


fun dp(dp: Int): Int {
    val metrics = Resources.getSystem().displayMetrics
    return (dp * metrics.density).toInt()
}