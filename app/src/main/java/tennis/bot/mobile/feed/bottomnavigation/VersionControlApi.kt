package tennis.bot.mobile.feed.bottomnavigation

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VersionControlApi {

	@GET("api/app-version")
	suspend fun getAppVersion(
		@Query("app") app: Int = 2,
		@Query("version") version: String // example: 1.0.0
	): Response<VersionControlResponse>
}

@Serializable
data class VersionControlResponse (
	val updated: Boolean, // suggest update
	val broken: Boolean // require update
)