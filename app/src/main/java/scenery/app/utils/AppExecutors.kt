package scenery.app.utils

import java.util.concurrent.Executors

private val WORKER_EXECUTOR = Executors.newSingleThreadExecutor()

/**
 * Utility method to run blocks on a dedicated background thread, used for work stuff.
 */
fun runOnWorkerThread(f: () -> Unit) {
    WORKER_EXECUTOR.execute(f)
}