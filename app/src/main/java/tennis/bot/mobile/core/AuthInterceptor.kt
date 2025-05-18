package tennis.bot.mobile.core

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

// Тут мы прикрепляем авторизационный токен к запросу
@Singleton
class AuthInterceptor @Inject constructor(
    // todo тут синглтоновый репо с токенами
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // тут ловим set-cookie и сохраняем токены в репозиторий
        val request = chain.request().headers // как-то запихать авторизационный токен

        try {
            val response = chain.proceed(chain.request()) // todo fixme
        } catch (e: Exception) {
            // Если 401, то пытаемся ещё раз ебнуть запрос, но уже с refreshToken, и сохранить новые токены(auth и refresh) в репозиторий
            // Если и этот запрос обосрался, то надо выкинуть пользователя на экран авторизации.
        }

        Log.d("1234567", "intercept: ${response.headers.joinToString { it.toString() }}") //
        return response
    }
}