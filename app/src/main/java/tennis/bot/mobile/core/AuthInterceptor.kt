package tennis.bot.mobile.core

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

// Тут мы прикрепляем авторизационный токен к запросу
@Singleton
class AuthInterceptor @Inject constructor(
   private val tokenRepository: AuthTokenRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()// как-то запихать авторизационный токен
		var accessToken = tokenRepository.getAccessToken()

		val response = chain.proceed(newRequestWithAccessToken(accessToken, request))

		if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
			val newAccessToken = tokenRepository.getAccessToken()
			if (newAccessToken != accessToken) {
				return chain.proceed(newRequestWithAccessToken(accessToken, request))
			} else {
//				accessToken = refreshToken()
//				if (accessToken.isNullOrBlank()) {
//					sessionManager.logout()
//					return response
				}
				return chain.proceed(newRequestWithAccessToken(accessToken, request))
			}

		return response

//        val response: Response = try {
//            chain.proceed(chain.request()) // todo fixme
//        } catch (e: Exception) {
////             Если 401, то пытаемся ещё раз ебнуть запрос, но уже с refreshToken, и сохранить новые токены(auth и refresh) в репозиторий
////             Если и этот запрос обосрался, то надо выкинуть пользователя на экран авторизации.
//        }
//
//        Log.d("1234567", "intercept: ${response.headers.joinToString { it.toString() }}")

    }

	private fun newRequestWithAccessToken(accessToken: String?, request: Request): Request =
		request.newBuilder()
			.header("Authorization", "$accessToken")
			.build()

//	private suspend fun refreshToken(): String? {
//
//		synchronized(this) {
//			val refreshToken = tokenRepository.postLogin()
//			refreshToken?.let {
//				return sessionManager.refreshToken(refreshToken)
//			} ?: return null
//		}
//	}
}