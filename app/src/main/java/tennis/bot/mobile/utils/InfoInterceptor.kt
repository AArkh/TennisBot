package tennis.bot.mobile.utils

import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.userAgent
import javax.inject.Inject

class InfoInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequestBuilder = originalRequest.newBuilder()
        val userAgent = generateUserAgent()
        newRequestBuilder.header("User-Agent", userAgent)

        return chain.proceed(newRequestBuilder.build())
    }

    private fun generateUserAgent(): String {
        val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        val deviceInfo = "${Build.MANUFACTURER} ${Build.MODEL}"
        val androidVersion = Build.VERSION.RELEASE
        return "TennisBot/$appVersion ($deviceInfo; Android $androidVersion) $userAgent"
    }
}