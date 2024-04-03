package tennis.bot.mobile.onboarding.survey

import androidx.annotation.IntDef
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NewPlayerApi {

    @POST("api/new-player")
    suspend fun postNewPlayer(@Body newPlayer: NewPlayer, @Header("Authorization") authHeader: String): Response<NewPlayerResponse>
}

@Serializable
data class NewPlayerResponse(
    val id: Long,
    val name: String?,
    val surName: String?,
    val birthday: String,
    val isMale: Boolean,
    val telegram: String?,
    val photo: String?,
    val countryId: Long?,
    val cityId: Long?,
    val districtId: Long?,
    val experience: Int?,
    val initialRating: Int,
    val rating: Int?,
    val doublesRating: Int?,
    val games: Int?,
    val gamesWin: Int?,
    val isRightHand: Boolean?,
    val isOneBackhand: Boolean?,
    val mixedRating: Int?,
    val universalDoublesRating: Int?,
    val bonus: Int?,
    val bonusRank: Int?,
    val primaryLocation: Long?,
    val secondaryLocation: Long?,
    val racquet: Long?,
    val racquetDetail: String?,
    val shoes: Long?,
    val racquetStrings: Long?,
    val surface: Long?,
    val lastGame: String?,
    val premiumExpiration: String?
)

@Serializable
data class NewPlayer(
    val name: String,
    val surName: String,
    val phoneNumber: String?,
    val birthday: String,
    val isMale: Boolean,
    val countryId: Int,
    val cityId: Int?, //nullable
    val districtId: Int?,
    val telegram: String,
    val surveyAnswer: SurveyAnswers
) {
    @Serializable
    data class SurveyAnswers(
        @SurveyOptions val experience: Int,
        @SurveyOptions val forehand: Int,
        @SurveyOptions val backhand: Int,
        @SurveyOptions val slice: Int,
        @SurveyOptions val serve: Int,
        @SurveyOptions val net: Int,
        @SurveyOptions val speed: Int,
        @SurveyOptions val tournaments: Int,
        @SurveyOptions val prizes: Int
    )
}

@IntDef(0, 1, 2, 3, 4)
@Retention(AnnotationRetention.SOURCE)
annotation class SurveyOptions