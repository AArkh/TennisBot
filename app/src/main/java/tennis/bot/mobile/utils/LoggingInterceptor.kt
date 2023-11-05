package tennis.bot.mobile.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

class LoggingInterceptor @Inject constructor() : Interceptor {

    private val interceptor = HttpLoggingInterceptor { Log.d("NETWORK", it) }

    init {
        interceptor.level = HttpLoggingInterceptor.Level.BODY
    }

    override fun intercept(chain: Interceptor.Chain): Response = interceptor.intercept(chain)
}