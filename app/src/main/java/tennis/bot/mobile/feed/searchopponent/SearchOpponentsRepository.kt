package tennis.bot.mobile.feed.searchopponent

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import tennis.bot.mobile.R
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.utils.showToast
import java.io.IOException
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

	private var userInput: String? = null

	fun recordUserInput(input: CharSequence) {
		userInput = input.toString()
	}


	@WorkerThread
	suspend fun getOpponents(): OpponentsBasicResponse? {

		val response = userInput?.let { api.getOpponents(it) }
		if (response?.code() == 200) return response.body()
		if (response?.code() == 404) context.showToast("Something went wrong")

		return OpponentsBasicResponse(0, emptyList())
	}

	@WorkerThread
	suspend fun getOpponentItems(): List<OpponentItem> {
		val opponentItems = getOpponents()?.items
		return if (!opponentItems.isNullOrEmpty()) opponentItems.convertToOpponentItemList()
		else emptyList()
	}

	inner class OpponentsDataSource : PagingSource<Int, OpponentItem>() {

		override fun getRefreshKey(state: PagingState<Int, OpponentItem>): Int? {
			TODO("Not yet implemented")
		}

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OpponentItem> {
			val position = params.key ?: 0
			return try {
				val response = userInput?.let { api.getOpponents(it, position) }
				val matchItemsList = response?.body()?.items?.convertToOpponentItemList()
				val nextPosition = position + 20
				if (position >= (response?.body()?.totalCount ?: 0)) {
					Log.d("OpponentsDataSource", "Reached the end of data")
					return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
				} else {

					Log.d("OpponentsDataSource", "Loading page starting from position: $nextPosition")
					LoadResult.Page(
						data = matchItemsList!!,
						prevKey = if (position == 0) null else position - params.loadSize,
						nextKey = if (nextPosition >= (response.body()?.totalCount ?: 0)) null else nextPosition
					)
				}


			} catch (exception: IOException) {
				return LoadResult.Error(exception)
			} catch (exception: HttpException) {
				return LoadResult.Error(exception)
			}
		}
	}

	private suspend fun List<OpponentResponseItem>.convertToOpponentItemList(): List<OpponentItem> {
		return map { opponentResponseItem ->
			val experience =
				userProfileAndEnumsRepository.getEnumsById(listOf(Pair(PLAYER_EXPERIENCE_ENUM_TITLE, opponentResponseItem.experience)))[0]

			OpponentItem(
				id = opponentResponseItem.id,
				profilePicture = opponentResponseItem.photo,
				nameSurname = opponentResponseItem.name,
				infoPanel = context.getString(
					R.string.player_info_panel,
					opponentResponseItem.rating,
					opponentResponseItem.doublesRating,
					experience,
					"${opponentResponseItem.games} матчей")
			)
		}

	}
}