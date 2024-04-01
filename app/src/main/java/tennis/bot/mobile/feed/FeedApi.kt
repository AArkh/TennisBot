package tennis.bot.mobile.feed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.profile.matches.TennisSetNetwork

interface FeedApi {

	@GET("api/activity")
	suspend fun getActivities(
		@Query("skip") skip:Int = DEFAULT_SKIP,
		@Query("limit") limit:Int = DEFAULT_LIMIT
	): Response<ActivityBasicResponse>

	companion object{
		const val DEFAULT_SKIP = 0
		const val DEFAULT_LIMIT = 20
	}
}

@Serializable
data class ActivityBasicResponse(
	val totalCount: Int,
	val items: List<PostData>
)

@Serializable
data class PostData(
	val id: Long,
	val postType: Int,
	val totalLikes: Int,
	val liked: Boolean,
	val post: PostParent?,
	val addedAt: String?
): CoreUtilsItem()

@Serializable
sealed class PostParent(
//	val postType: Int
) {

@Serializable
@SerialName("1")
data class NewPlayerPost(
	val type: Int, // 1
	val id: Long,
	val name: String,
	val isMale: Boolean,
	val cityId: Int,
	val primaryLocation: Int?,
	val secondaryLocation: Int?,
	val cityOther: String?,
	val experience: Int,
	val rating: Int,
	val btrp: Double,
	val username: String?,
	val picFile: String?
): PostParent()

@Serializable
@SerialName("2")
data class MatchRequestPost(
	val type: Int, // 2
	val pay: Int,
	val btrp: Double,
	val cityId: Int,
	val districtId: Int,
	val comment: String,
	val gameType: Int,
	val playerId: Long,
	val playerName: String,
	val playerPhoto: String,
	val playerExperience: Int,
	val playerIsMale: Boolean,
	val playerUsername: String?,
	val playerRating: Int,
	val matchesPlayed: Int,
	val opponentsCount: Int,
	val opponentsPower: Int,
	val date: String?,
	val gameOrderId: Int
): PostParent()

@Serializable
@SerialName("3")
data class ScorePost(
	val type: Int, // 3
	val id: Long,
	val creatorId: Long,
	val photo: String?,
	@SerialName("Video") val video: String?,
	val sets: List<TennisSetNetwork>,
	val player1: PlayerPostData?,
	val player2: PlayerPostData?,
	val player3: PlayerPostData?,
	val player4: PlayerPostData?,
	val surface: String?,
	val duration: String?,
	val matchball: Int,
	val matchWon: Boolean,
	val averageScore: String?
): PostParent()

@Serializable
data class PlayerPostData(
	val id: Long,
	val name: String,
	val photo: String?,
	val isMale: Boolean,
	val bonus: Int,
	val bonusChange: Int,
	val powerOld: Int,
	val powerNew: Int,
	val powerPositionOld: Int,
	val powerPositionNew: Int,
	val headToHead: Int
)
}