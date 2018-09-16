package scenery.app.utils

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream


fun saveBitmap(bitmap: Bitmap, filename: String) {
    val root = Environment.getExternalStorageDirectory().absolutePath + File.separator + "tensorflow"
    Log.i("ImageUtils#saveBitmap", "Saving %dx%d bitmap to %s.".format(bitmap.width, bitmap.height, root))
    val myDir = File(root)

    if (!myDir.mkdirs()) {
        Log.i("ImageUtils#saveBitmap", "Make dir failed")
    }

    val file = File(myDir, filename)
    if (file.exists()) {
        file.delete()
    }
    try {
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 99, out)
        out.flush()
        out.close()
    } catch (e: Exception) {
        Log.e("ImageUtils#saveBitmap", "Exception!", e)
    }

}