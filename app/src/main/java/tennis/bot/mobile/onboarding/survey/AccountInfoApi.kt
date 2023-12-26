package tennis.bot.mobile.onboarding.survey

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface AccountInfoApi {

	@POST("api/core-account/register")
	suspend fun postRegister(@Body register: Register): Response<RegisterResponse>

	@FormUrlEncoded
	@Headers("Content-Type:$CONTENT_TYPE_HEADER")
	@POST("connect/token")
	suspend fun loginUser(
		@Field("grant_type") grantType: String,
		@Field("client_id") clientId: String,
		@Field("username") username: String,
		@Field("password") password: String,
		@Field("client_secret") clientSecret: String,
		@Field("scope") scope: String,
		@Field("audience") audience: String
	): Response<TokenResponse>

	@FormUrlEncoded
	@Headers("Content-Type:$CONTENT_TYPE_HEADER")
	@POST("connect/token")
	fun updateUserToken(
		@Field("grant_type") grantType: String,
		@Field("client_id") clientId: String,
		@Field("refresh_token") refreshToken: String,
		@Field("scope") scope: String,
		@Field("audience") audience: String
	): Call<TokenResponse>
}

const val CONTENT_TYPE_HEADER: String = "application/x-www-form-urlencoded"

@Serializable
data class TokenResponse(
	@SerialName("access_token") val accessToken: String,
	@SerialName("token_type") val tokenType: String,
	@SerialName("expires_in") val expiresIn: String,
	@SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class Register(
	val phoneNumber: String?,
	val password: String?,
	val smsVerifyCode: String?
)

@Serializable
data class RegisterResponse(
	val userName: String,
	val phoneNumber: String,
	val phoneNumberConfirmed: Boolean,
	val creationTime: String
)