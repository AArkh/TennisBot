package tennis.bot.mobile.utils

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object AppCoroutineScopes {

    val appWorkerScope = CoroutineScope(SupervisorJob() + getCoroutineExceptionHandler("1234567 AppScope"))
    val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Main + getCoroutineExceptionHandler("1234567 MainScope"))

    private fun getCoroutineExceptionHandler(tag: String): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            Log.e(tag, throwable.message, throwable)
        }
    }
}