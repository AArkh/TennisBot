package tennis.bot.mobile.feed.searchopponent

import android.content.Context
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.R
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchOpponentsRepository @Inject constructor(
	private val api: OpponentsApi,
	private val userProfileAndEnumsRepository: UserProfileAndEnumsRepository,
	@ApplicationContext private val context: Context
) {

	companion object {
		const val PLAYER_EXPERIENCE_ENUM_TITLE = "playerExperience"
	}

	@WorkerThread
	suspend fun getOpponents(userInput: String, position: Int): OpponentsBasicResponse? {

		val response = api.getOpponents(userInput, position)
		if (response.code() == 200) return response.body()
		if (response.code() == 404) context.showToast("Something went wrong")

		return OpponentsBasicResponse(0, emptyList())
	}

	suspend fun convertToOpponentItemList(list: List<OpponentResponseItem>): List<OpponentItem> {
		return list.map { opponentResponseItem ->
			val experience =
				userProfileAndEnumsRepository.getEnumsById(listOf(Pair(PLAYER_EXPERIENCE_ENUM_TITLE, opponentResponseItem.experience)))[0]

			OpponentItem(
				id = opponentResponseItem.id,
				profilePicture = opponentResponseItem.photoUrl,
				nameSurname = opponentResponseItem.name,
				infoPanel = context.getString(
					R.string.player_info_panel,
					opponentResponseItem.rating,
					opponentResponseItem.doublesRating,
					experience,
					"${opponentResponseItem.games ?: 0} матчей")
			)
		}
	}
}