package tennis.bot.mobile.feed.activityfeed

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface LikesApi {

	@POST("api/activity/{postId}/like")
	suspend fun postLike(
		@Path("postId") postId: Int
	): Response<Void>

	@POST("api/activity/{postId}/unlike")
	suspend fun postUnlike(
		@Path("postId") postId: Int
	): Response<Void>
}