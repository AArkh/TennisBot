package tennis.bot.mobile.feed.insertscore

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import tennis.bot.mobile.utils.uriToFile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertScoreRepository @Inject constructor(
	private val insertScoreApi: InsertScoreApi,
	private val mediaApi: MediaApi,
	@ApplicationContext private val context: Context
) {

	@WorkerThread
	suspend fun postAddScore(postBody: InsertScoreItem): Boolean {
		val response = kotlin.runCatching {
			insertScoreApi.postAddScore(postBody)
		}.getOrElse { return false }

		return response.isSuccessful
	}

	@WorkerThread
	suspend fun postPhoto(photo: Uri?): String? {
		if (photo == null) return null

		val photoFile = uriToFile(context, photo)
		val requestPhotoFile = photoFile?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
		val photoPart = MultipartBody.Part.createFormData("Content", photoFile?.name, requestPhotoFile!!)

		val response = mediaApi.postPhotoContent(
			Content = photoPart,
			name = ""
		)

		if (response.code() == 204) {
			Log.d("123456", "photo was posted")
		} else if (response.code() == 400) {
			Log.d("123456", "photo has crashed and burned")
		}

		return response.body()
	}

	@WorkerThread
	suspend fun postVideo(video: Uri?): String? {
		if (video == null) return null

		val videoFile = uriToFile(context, video)
		val requestVideoFile = videoFile?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
		val videoPart = MultipartBody.Part.createFormData("Content", videoFile?.name, requestVideoFile!!)

		val response = mediaApi.postVideoContent(
			Content = videoPart,
			name = ""
		)

		if (response.code() == 204) {
			Log.d("123456", "video was posted")
		} else if (response.code() == 400) {
			Log.d("123456", "video has crashed and burned")
		}

		return response.body()
	}
}