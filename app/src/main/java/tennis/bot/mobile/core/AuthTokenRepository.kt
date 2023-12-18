package tennis.bot.mobile.core

import android.util.Log
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import tennis.bot.mobile.onboarding.survey.AccountInfoApi
import tennis.bot.mobile.onboarding.survey.TokenResponse
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenRepository @Inject constructor(
	private val api: AccountInfoApi,
) {

	@Volatile
	private var currentToken: TokenResponse? = null
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
			grantType = "refresh_token",
			clientId = "Core_Swagger",
			refreshToken = refreshToken,
			scope = "roles offline_access",
			audience = "Core"
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