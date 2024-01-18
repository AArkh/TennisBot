package tennis.bot.mobile.onboarding.photopick

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotoPickApi {

	@Multipart
	@POST("api/tennis-players/profile-picture")
	suspend fun postProfilePicture(
		@Query("registerPhoto") registerPhoto: Boolean = false,
		@Part Content: MultipartBody.Part,
		@Part("Name") name: String,
		@Header("Authorization") authHeader: String
	): Response<Unit> // 204 means good

	@POST("api/tennis-players/set-default-profile-picture/{defaultPhotoId}")
	suspend fun postDefaultProfilePictureId(
		@Path("defaultPhotoId") defaultPhotoId: Int,
		@Header("Authorization") authHeader: String
	): Response<Unit> // 204 means good
}

