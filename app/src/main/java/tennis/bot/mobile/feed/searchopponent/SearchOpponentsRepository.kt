package tennis.bot.mobile.feed.searchopponent

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.activityfeed.formatLocationDataForPost
import tennis.bot.mobile.onboarding.location.LocationDataMapper
import tennis.bot.mobile.onboarding.location.LocationRepository
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchOpponentsRepository @Inject constructor(
	private val api: OpponentsApi,
	private val userProfileAndEnumsRepository: UserProfileAndEnumsRepository,
	private val locationRepository: LocationRepository,
	private val locationDataMapper: LocationDataMapper,
	@ApplicationContext private val context: Context
) {

	companion object {
		const val PLAYER_EXPERIENCE_ENUM_TITLE = "playerExperience"
	}

	@WorkerThread
	suspend fun getOpponents(userInput: String, position: Int, checkInvites: Boolean = false): OpponentsBasicResponse? {
		val response = api.getOpponents(userInput, position, checkInvites = checkInvites)
		if (response.isSuccessful) return response.body()
		else {
			FirebaseCrashlytics.getInstance().log("getOpponents code ${response.code()} and message: ${response.message()}")
			context.showToast("Something went wrong")
		}

		return OpponentsBasicResponse(0, emptyList())
	}

	suspend fun convertToOpponentItemList(list: List<OpponentResponseItem>, withLocation: Boolean = false): List<OpponentItem> {
		return list.map { opponentResponseItem ->
			val experience =
				userProfileAndEnumsRepository.getEnumById(Pair(PLAYER_EXPERIENCE_ENUM_TITLE, opponentResponseItem.experience))
			if (!withLocation) {
				OpponentItem(
					id = opponentResponseItem.id,
					profilePicture = opponentResponseItem.photoUrl,
					nameSurname = opponentResponseItem.name,
					infoPanel = context.getString(
						R.string.player_info_panel,
						opponentResponseItem.rating,
						opponentResponseItem.doublesRating,
						experience,
						context.resources.getQuantityString(R.plurals.matches, opponentResponseItem.games ?: 0, opponentResponseItem.games ?: 0)),
					isInvited = opponentResponseItem.isInvited
				)
			} else {
				OpponentItem(
					id = opponentResponseItem.id,
					locationSubtitle = formatLocationDataForPost(opponentResponseItem.cityId, opponentResponseItem.districtId, locationRepository, locationDataMapper),
					profilePicture = opponentResponseItem.photoUrl,
					nameSurname = opponentResponseItem.name,
					infoPanel = context.getString(
						R.string.player_info_panel,
						opponentResponseItem.rating,
						opponentResponseItem.doublesRating,
						experience,
						context.resources.getQuantityString(R.plurals.matches, opponentResponseItem.games ?: 0, opponentResponseItem.games ?: 0)),
					isInvited = opponentResponseItem.isInvited
				)
			}
		}
	}
}