package tennis.bot.mobile.core

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

// Тут мы ловим токен авторизации
@Singleton
class NewPlayerInterceptor @Inject constructor(
    // todo тут синглтоновый репо с токенами
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // тут ловим set-cookie и сохраняем токены в репозиторий
        val response = chain.proceed(chain.request()) // todo fixme

        Log.d("1234567", "intercept: ${response.headers.joinToString { it.toString() }}") //
        return response
    }
}