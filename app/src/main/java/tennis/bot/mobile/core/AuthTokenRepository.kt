package tennis.bot.mobile.core

import tennis.bot.mobile.onboarding.survey.TokenResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenRepository @Inject constructor() {

	private lateinit var currentToken: TokenResponse

	fun recordToken(token: TokenResponse) {
		currentToken = token
	}

	fun getAccessToken(): String {
		return "Bearer ${currentToken.accessToken}"
	}
}