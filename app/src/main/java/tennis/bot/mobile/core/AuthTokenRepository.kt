package tennis.bot.mobile.core

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import tennis.bot.mobile.onboarding.survey.OnboardingRepository
import tennis.bot.mobile.onboarding.survey.RegisterAndLoginApi
import tennis.bot.mobile.onboarding.survey.TokenResponse
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenRepository @Inject constructor(
	private val api: RegisterAndLoginApi,
	@ApplicationContext private val context: Context
) {

	companion object {
		const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
		const val TOKEN_TYPE_KEY = "TOKEN_TYPE_KEY"
		const val EXPIRES_IN_KEY = "EXPIRES_IN_KEY"
		const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY"
		private const val CLIENT_SECRET = "Aitq2BWbQDR8pOiOwFS5SXGlss93xLlY"
		private const val GRANT_TYPE = "refresh_token"
		private const val CLIENT_ID = "Core_Android"
		private const val SCOPE = "roles offline_access"
		private const val AUDIENCE = "Core"
	}

	private val sharedPreferences = context.getSharedPreferences("Tokens", Context.MODE_PRIVATE)

	@Volatile
	private var currentToken: TokenResponse? =
		TokenResponse(
		accessToken = sharedPreferences.getString(ACCESS_TOKEN_KEY, null),
		tokenType = sharedPreferences.getString(TOKEN_TYPE_KEY, null),
		expiresIn = sharedPreferences.getString(EXPIRES_IN_KEY, null),
		refreshToken = sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
	)
		set(value) {
			field = value
			if (value == null && field != null) {
				_unAuthEventsFlow.tryEmit(Unit)
			}
		}

	private val _unAuthEventsFlow = MutableSharedFlow<Unit>()
	val unAuthEventsFlow: SharedFlow<Unit> = _unAuthEventsFlow.asSharedFlow()

	@Synchronized
	fun recordToken(token: TokenResponse?) {
		currentToken = token

		sharedPreferences.edit().putString(ACCESS_TOKEN_KEY, token?.accessToken).apply()
		sharedPreferences.edit().putString(TOKEN_TYPE_KEY, token?.tokenType).apply()
		sharedPreferences.edit().putString(EXPIRES_IN_KEY, token?.expiresIn).apply()
		sharedPreferences.edit().putString(REFRESH_TOKEN_KEY, token?.refreshToken).apply()
	}

	@Synchronized
	fun getAccessToken(): String? {
		val token = currentToken
		return if (token != null) {
			"Bearer ${token.accessToken}"
		} else null
	}

	@Synchronized
	fun getRefreshToken(): String? {
		return currentToken?.refreshToken
	}

	@Throws(IllegalStateException::class, IOException::class)
	@WorkerThread
	@Synchronized
	fun updateToken() {
		val refreshToken = getRefreshToken() ?: throw IllegalStateException("No refresh token found")
		val response = api.updateUserToken(
			grantType = GRANT_TYPE,
			clientId = CLIENT_ID,
			refreshToken = refreshToken,
			clientSecret = CLIENT_SECRET,
			scope = SCOPE,
			audience = AUDIENCE
		).execute()

		if (response.code() == 200) {
			Log.d("1234567", "token has been recorded")
			recordToken(
				TokenResponse(
					accessToken = response.body()!!.accessToken,
					tokenType = response.body()!!.tokenType,
					expiresIn = response.body()!!.expiresIn,
					refreshToken = response.body()!!.refreshToken
				)
			)
		} else {
			throw IOException("Failed to update token")
		}
	}
}