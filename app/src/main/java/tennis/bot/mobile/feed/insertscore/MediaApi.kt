package tennis.bot.mobile.feed.insertscore

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MediaApi {

	@Multipart
	@POST("api/content-manager/upload-picture")
	suspend fun postPhotoContent(
		@Part Content: MultipartBody.Part,
		@Part("Name") name: String,
	): Response<String> // 204 means good

	@Multipart
	@POST("api/content-manager/upload-video")
	suspend fun postVideoContent(
		@Part Content: MultipartBody.Part,
		@Part("Name") name: String,
	): Response<String> // 204 means good
}