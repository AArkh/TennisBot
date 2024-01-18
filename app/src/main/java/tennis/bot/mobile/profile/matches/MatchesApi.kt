package tennis.bot.mobile.profile.matches

import kotlinx.serialization.Serializable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface MatchesApi {

    @GET("/api/games/scores")
    suspend fun getScores(
        @Query("skip") skip: Int = DEFAULT_SKIP,
        @Query("limit") limit: Int = DEFAULT_LIMIT // сколько приходит айтемов на страницу || верхний предел после skip
    ): Call<List<MatchResponseItem>>

    // initial getScores(0, 20),
    // content(list.size == 20) -> getScores(content.size, 20),
    // content(list.size == 40) -> getScores(content.size, 20),
    // content(list.size == 60) -> getScores(content.size, 20),
    // content(list.size == 80) -> getScores(content.size, 20),

    @Headers(
        "secretTelegramToken: superSecret",
        "playerId: 23392545"
    )
    @GET("/api/games/scores")
    suspend fun getTestScores(
        @Query("skip") skip: Int = DEFAULT_SKIP,
        @Query("limit") limit: Int = DEFAULT_LIMIT
    ): Call<List<MatchResponseItem>>

    companion object {
        const val DEFAULT_SKIP = 0
        const val DEFAULT_LIMIT = 20
    }
}

@Serializable
data class MatchResponseItem(
    val id: Int,
    val win: Boolean,
    val isDouble: Boolean,
    val headToHead1: Int,
    val headToHead2: Int,
    val playedAt: String,
    val photo: String?,
    val video: String?,
    val gameSets: List<GameSet>,
    val players: List<Player>
)

@Serializable
data class GameSet(
    val score1: Int,
    val score2: Int,
    val scoreTie1: Int?,
    val scoreTie2: Int?
)

@Serializable
data class Player(
    val id: Int,
    val name: String,
    val photo: String?,
    val oldRating: Int,
    val rating: Int
)


