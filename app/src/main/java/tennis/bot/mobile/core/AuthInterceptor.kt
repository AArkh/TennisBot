package tennis.bot.mobile.core

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import tennis.bot.mobile.onboarding.survey.AccountInfoRepository
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton

// Тут мы прикрепляем авторизационный токен к запросу
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenRepository: AuthTokenRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()// как-то запихать авторизационный токен
        val cachedAccessToken = tokenRepository.getAccessToken()
        if (cachedAccessToken != null) {
            val response = chain.proceed(request.newRequestWithAccessToken(cachedAccessToken))
            return if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                request.retryWithTokenUpdate(chain)
            } else {
                response
            }
        } else {
            return request.retryWithTokenUpdate(chain)
        }
    }

    @Synchronized
    private fun Request.retryWithTokenUpdate(chain: Interceptor.Chain): Response {
        tokenRepository.updateToken()
        val updatedToken = tokenRepository.getAccessToken()
        return chain.proceed(newRequestWithAccessToken(updatedToken))
    }

    private fun Request.newRequestWithAccessToken(accessToken: String?): Request = newBuilder()
        .removeHeader("Authorization")
        .header("Authorization", "$accessToken")
        .build()
}